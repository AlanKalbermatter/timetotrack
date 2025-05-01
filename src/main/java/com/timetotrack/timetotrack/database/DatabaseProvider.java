package com.timetotrack.timetotrack.database;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgBuilder;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class DatabaseProvider {

    public static Pool createPgPool(Vertx vertx) {
        JsonObject dbConfig = loadDbConfig();

        PgConnectOptions connectOptions = new PgConnectOptions()
                .setPort(dbConfig.getInteger("port"))
                .setHost(dbConfig.getString("host"))
                .setDatabase(dbConfig.getString("database"))
                .setUser(dbConfig.getString("user"))
                .setPassword(dbConfig.getString("password"));

        PoolOptions poolOptions = new PoolOptions()
                .setMaxSize(dbConfig.getInteger("maxPoolSize", 5)); // Default 5

        return PgBuilder.pool()
                .with(poolOptions)
                .connectingTo(connectOptions)
                .using(vertx)
                .build();
    }

    private static JsonObject loadDbConfig() {
        try (InputStream is = DatabaseProvider.class.getClassLoader().getResourceAsStream("dbconfig.json")) {
            if (is == null) {
                throw new RuntimeException("Cannot find dbconfig.json in resources");
            }
            String content = new Scanner(is, StandardCharsets.UTF_8).useDelimiter("\\A").next();
            return new JsonObject(content);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load database configuration", e);
        }
    }
}
