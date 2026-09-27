package com.timetotrack.timetotrack.database;

import com.timetotrack.timetotrack.support.IntegrationTest;
import io.vertx.pgclient.PgException;
import io.vertx.sqlclient.Tuple;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneOffset;

import static com.timetotrack.timetotrack.support.Await.awaitFailure;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class SchemaTest extends IntegrationTest {

    private static final Instant NINE = Instant.parse("2026-01-01T09:00:00Z");

    @Test
    void allowsOnlyOneRunningTimerPerUser() {
        int userId = fixtures.user("ana");
        int projectId = fixtures.project(fixtures.customer("Acme"), "Web");
        fixtures.entry(userId, projectId, NINE, null);

        Throwable failure = awaitFailure(insertEntry(userId, projectId, NINE.plusSeconds(3600), null));

        assertEquals("23505", assertInstanceOf(PgException.class, failure).getSqlState());
    }

    @Test
    void rejectsEntriesThatEndBeforeTheyStart() {
        int userId = fixtures.user("ana");
        int projectId = fixtures.project(fixtures.customer("Acme"), "Web");

        Throwable failure = awaitFailure(insertEntry(userId, projectId, NINE, NINE.minusSeconds(60)));

        assertEquals("23514", assertInstanceOf(PgException.class, failure).getSqlState());
    }

    @Test
    void blocksDeletingCustomersThatHaveProjects() {
        int customerId = fixtures.customer("Acme");
        fixtures.project(customerId, "Web");

        Throwable failure = awaitFailure(pool.preparedQuery("DELETE FROM customer WHERE customer_id = $1")
                .execute(Tuple.of(customerId)));

        assertEquals("23503", assertInstanceOf(PgException.class, failure).getSqlState());
    }

    private io.vertx.core.Future<?> insertEntry(int userId, int projectId, Instant from, Instant to) {
        return pool.preparedQuery("INSERT INTO time_entry (user_id, project_id, from_time, to_time) VALUES ($1, $2, $3, $4)")
                .execute(Tuple.of(userId, projectId, from.atOffset(ZoneOffset.UTC), to == null ? null : to.atOffset(ZoneOffset.UTC)));
    }
}
