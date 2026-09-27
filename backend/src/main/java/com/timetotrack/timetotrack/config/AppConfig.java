package com.timetotrack.timetotrack.config;

import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public record AppConfig(String profile, int gatewayPort, ServicePorts ports, DbConfig db, String jwtSecret) {

    public static final String DEV_JWT_SECRET = "dev-only-insecure-jwt-secret-change-me";
    private static final String DEV_PROFILE = "dev";

    public static AppConfig fromEnv(Map<String, String> env) {
        String profile = env.getOrDefault("APP_PROFILE", DEV_PROFILE);
        String jwtSecret = env.get("JWT_SECRET");
        if (jwtSecret == null || jwtSecret.isBlank()) {
            if (!DEV_PROFILE.equals(profile)) {
                throw new IllegalStateException("JWT_SECRET must be set when APP_PROFILE=" + profile);
            }
            jwtSecret = DEV_JWT_SECRET;
        }
        return new AppConfig(profile, intEnv(env, "HTTP_PORT", 8080), ServicePorts.DEFAULT, dbFromEnv(env), jwtSecret);
    }

    private static DbConfig dbFromEnv(Map<String, String> env) {
        JsonObject defaults = loadDbDefaults();
        return new DbConfig(
                env.getOrDefault("DB_HOST", defaults.getString("host")),
                intEnv(env, "DB_PORT", defaults.getInteger("port")),
                env.getOrDefault("DB_NAME", defaults.getString("database")),
                env.getOrDefault("DB_USER", defaults.getString("user")),
                env.getOrDefault("DB_PASSWORD", defaults.getString("password")),
                defaults.getInteger("maxPoolSize", 5));
    }

    private static int intEnv(Map<String, String> env, String key, int fallback) {
        String value = env.get(key);
        if (value == null || value.isBlank()) {
            return fallback;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalStateException(key + " must be an integer, got: " + value);
        }
    }

    private static JsonObject loadDbDefaults() {
        try (InputStream is = AppConfig.class.getClassLoader().getResourceAsStream("dbconfig.json")) {
            if (is == null) {
                throw new IllegalStateException("dbconfig.json not found on the classpath");
            }
            return new JsonObject(new String(is.readAllBytes(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read dbconfig.json", e);
        }
    }
}
