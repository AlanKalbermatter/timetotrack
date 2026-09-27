package com.timetotrack.timetotrack.support;

import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Tuple;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static com.timetotrack.timetotrack.support.Await.await;

/** Inserts rows directly with SQL, bypassing services, to arrange test state. */
public final class Fixtures {

    private final Pool pool;

    public Fixtures(Pool pool) {
        this.pool = pool;
    }

    public int user(String username) {
        return await(pool.preparedQuery(
                        "INSERT INTO \"user\" (username, email, full_name, password_hash) VALUES ($1, $2, $3, 'not-a-real-hash') RETURNING user_id")
                .execute(Tuple.of(username, username + "@example.com", username)))
                .iterator().next().getInteger("user_id");
    }

    public int customer(String name) {
        return await(pool.preparedQuery("INSERT INTO customer (customer_name) VALUES ($1) RETURNING customer_id")
                .execute(Tuple.of(name)))
                .iterator().next().getInteger("customer_id");
    }

    public int project(int customerId, String name) {
        return await(pool.preparedQuery("INSERT INTO projects (project_name, customer_id) VALUES ($1, $2) RETURNING project_id")
                .execute(Tuple.of(name, customerId)))
                .iterator().next().getInteger("project_id");
    }

    public long entry(int userId, int projectId, Instant from, Instant to) {
        return await(pool.preparedQuery(
                        "INSERT INTO time_entry (user_id, project_id, from_time, to_time) VALUES ($1, $2, $3, $4) RETURNING time_entry_id")
                .execute(Tuple.of(userId, projectId, utc(from), to == null ? null : utc(to))))
                .iterator().next().getLong("time_entry_id");
    }

    private static OffsetDateTime utc(Instant instant) {
        return instant.atOffset(ZoneOffset.UTC);
    }
}
