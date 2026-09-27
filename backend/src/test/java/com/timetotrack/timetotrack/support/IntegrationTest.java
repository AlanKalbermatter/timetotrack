package com.timetotrack.timetotrack.support;

import com.timetotrack.timetotrack.database.DatabaseProvider;
import io.vertx.core.Vertx;
import io.vertx.sqlclient.Pool;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import static com.timetotrack.timetotrack.support.Await.await;

/** Base class for tests that need Postgres: one Vert.x + pool per class, a clean database per test. */
public abstract class IntegrationTest {

    protected static Vertx vertx;
    protected static Pool pool;
    protected static Fixtures fixtures;

    @BeforeAll
    static void startInfrastructure() {
        vertx = Vertx.vertx();
        pool = DatabaseProvider.createPgPool(vertx, TestDatabase.dbConfig());
        fixtures = new Fixtures(pool);
    }

    @AfterAll
    static void stopInfrastructure() {
        await(pool.close());
        await(vertx.close());
    }

    @BeforeEach
    void cleanDatabase() {
        TestDatabase.truncateAll(pool);
    }
}
