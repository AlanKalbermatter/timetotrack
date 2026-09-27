package com.timetotrack.timetotrack.support;

import com.timetotrack.timetotrack.config.DbConfig;
import io.vertx.sqlclient.Pool;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.MountableFile;

import static com.timetotrack.timetotrack.support.Await.await;

/** One Postgres 16 container per test JVM, initialised with the production schema.sql. */
public final class TestDatabase {

    private static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:16-alpine")
            .withCopyFileToContainer(
                    MountableFile.forClasspathResource("schema.sql"),
                    "/docker-entrypoint-initdb.d/01-schema.sql");

    static {
        // Testcontainers' Ryuk sidecar removes the container when the JVM exits.
        POSTGRES.start();
    }

    private TestDatabase() {
    }

    public static DbConfig dbConfig() {
        return new DbConfig(
                POSTGRES.getHost(),
                POSTGRES.getMappedPort(5432),
                POSTGRES.getDatabaseName(),
                POSTGRES.getUsername(),
                POSTGRES.getPassword(),
                4);
    }

    public static void truncateAll(Pool pool) {
        await(pool.query("TRUNCATE time_entry, projects, customer, \"user\" RESTART IDENTITY CASCADE").execute());
    }
}
