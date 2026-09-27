package com.timetotrack.timetotrack.dao;

import com.timetotrack.timetotrack.constant.TimeEntrySQL;
import com.timetotrack.timetotrack.database.Rows;
import com.timetotrack.timetotrack.model.TimeEntry;
import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static com.timetotrack.timetotrack.database.Timestamps.fromDb;
import static com.timetotrack.timetotrack.database.Timestamps.toDb;

/** Every query is scoped by user id: one user can never read or change another's entries. */
public class TimeEntryDao {

    private final Pool pool;

    public TimeEntryDao(Pool pool) {
        this.pool = pool;
    }

    public Future<List<TimeEntry>> findForUser(int userId, Instant from, Instant to, Instant now) {
        return pool.preparedQuery(TimeEntrySQL.SELECT_FOR_USER_IN_RANGE)
                .execute(Tuple.of(userId, toDb(from), toDb(to), toDb(now)))
                .map(rows -> Rows.map(rows, TimeEntryDao::toEntry));
    }

    public Future<Optional<TimeEntry>> findByIdForUser(long id, int userId) {
        return pool.preparedQuery(TimeEntrySQL.SELECT_BY_ID_FOR_USER)
                .execute(Tuple.of(id, userId))
                .map(rows -> Rows.first(rows).map(TimeEntryDao::toEntry));
    }

    public Future<Optional<TimeEntry>> findRunning(int userId) {
        return pool.preparedQuery(TimeEntrySQL.SELECT_RUNNING_FOR_USER)
                .execute(Tuple.of(userId))
                .map(rows -> Rows.first(rows).map(TimeEntryDao::toEntry));
    }

    /** @return the new entry id; {@code to == null} starts a running timer */
    public Future<Long> insert(int userId, int projectId, String description, Instant from, Instant to) {
        return pool.preparedQuery(TimeEntrySQL.INSERT_ONE)
                .execute(Tuple.of(userId, projectId, description, toDb(from), toDb(to)))
                .map(rows -> rows.iterator().next().getLong("time_entry_id"));
    }

    /** @return the id of the entry that was stopped, or empty when no timer was running */
    public Future<Optional<Long>> stopRunning(int userId, Instant now) {
        return pool.preparedQuery(TimeEntrySQL.STOP_RUNNING_FOR_USER)
                .execute(Tuple.of(userId, toDb(now)))
                .map(rows -> Rows.first(rows).map(row -> row.getLong("time_entry_id")));
    }

    /** @return whether a row owned by the user was deleted */
    public Future<Boolean> deleteForUser(long id, int userId) {
        return pool.preparedQuery(TimeEntrySQL.DELETE_FOR_USER)
                .execute(Tuple.of(id, userId))
                .map(rows -> rows.rowCount() > 0);
    }

    private static TimeEntry toEntry(Row row) {
        return new TimeEntry(
                row.getLong("time_entry_id"),
                row.getInteger("user_id"),
                row.getInteger("project_id"),
                row.getString("project_name"),
                row.getString("description"),
                fromDb(row.getOffsetDateTime("from_time")),
                fromDb(row.getOffsetDateTime("to_time")));
    }
}
