package com.timetotrack.timetotrack.config;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppConfigTest {

    @Test
    void devProfileFallsBackToDefaults() {
        AppConfig config = AppConfig.fromEnv(Map.of());

        assertEquals("dev", config.profile());
        assertEquals(8080, config.gatewayPort());
        assertEquals(ServicePorts.DEFAULT, config.ports());
        assertEquals(AppConfig.DEV_JWT_SECRET, config.jwtSecret());
        assertEquals(new DbConfig("localhost", 5432, "goldentimer", "goldentimer", "goldentimer123", 5), config.db());
    }

    @Test
    void environmentOverridesDefaults() {
        AppConfig config = AppConfig.fromEnv(Map.of(
                "HTTP_PORT", "9000",
                "DB_HOST", "db",
                "DB_PORT", "6543",
                "DB_NAME", "ttt",
                "DB_USER", "u",
                "DB_PASSWORD", "p",
                "JWT_SECRET", "s3cret"));

        assertEquals(9000, config.gatewayPort());
        assertEquals(new DbConfig("db", 6543, "ttt", "u", "p", 5), config.db());
        assertEquals("s3cret", config.jwtSecret());
    }

    @Test
    void nonDevProfileRequiresJwtSecret() {
        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> AppConfig.fromEnv(Map.of("APP_PROFILE", "docker")));

        assertTrue(error.getMessage().contains("JWT_SECRET"));
    }

    @Test
    void rejectsNonNumericPort() {
        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> AppConfig.fromEnv(Map.of("DB_PORT", "abc")));

        assertTrue(error.getMessage().contains("DB_PORT"));
    }
}
