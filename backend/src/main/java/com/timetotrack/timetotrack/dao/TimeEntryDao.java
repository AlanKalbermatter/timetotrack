package com.timetotrack.timetotrack.dao;

import com.timetotrack.timetotrack.constant.TimeEntrySQL;
import com.timetotrack.timetotrack.model.TimeEntry;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.sqlclient.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TimeEntryDao {

    private final Pool client;
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeEntryDao.class);

    public TimeEntryDao(Pool client) {
        this.client = client;
    }

    public void fetchAll(Handler<AsyncResult<List<TimeEntry>>> resultHandler) {
        client.query(TimeEntrySQL.SELECT_ALL).execute(ar -> {
            if (ar.succeeded()) {
                List<TimeEntry> entries = new ArrayList<>();
                for (Row row : ar.result()) {
                    entries.add(mapRowToTimeEntry(row));
                }
                resultHandler.handle(Future.succeededFuture(entries));
            } else {
                resultHandler.handle(Future.failedFuture(ar.cause()));
            }
        });
    }

    public void fetchById(long id, Handler<AsyncResult<TimeEntry>> resultHandler) {
        client.preparedQuery(TimeEntrySQL.SELECT_BY_ID)
                .execute(Tuple.of(id), ar -> {
                    if (ar.succeeded()) {
                        TimeEntry timeEntry = mapRowToTimeEntry(ar.result().iterator().next());
                        LOGGER.info("Fetched Time Entry by ID {}", id);
                        resultHandler.handle(Future.succeededFuture(timeEntry));
                    } else {
                        LOGGER.warn("Time Entry with id {} not found", id, ar.cause());
                        resultHandler.handle(Future.failedFuture(ar.cause()));
                    }
                });
    }

    public void create(TimeEntry entry, Handler<AsyncResult<TimeEntry>> resultHandler) {
        client.preparedQuery(TimeEntrySQL.INSERT_ONE)
                .execute(Tuple.of(entry.getFromTime(), entry.getToTime(), entry.getProjectId(), entry.getUserId()), ar -> {
                    if (ar.succeeded()) {
                        Row row = ar.result().iterator().next();
                        entry.setId(row.getLong("time_entry_id"));
                        resultHandler.handle(Future.succeededFuture(entry));
                    } else {
                        resultHandler.handle(Future.failedFuture(ar.cause()));
                    }
                });
    }

    public void update(TimeEntry entry, Handler<AsyncResult<TimeEntry>> resultHandler) {
        client.preparedQuery(TimeEntrySQL.UPDATE_ONE)
                .execute(Tuple.of(
                        entry.getFromTime(),
                        entry.getToTime(),
                        entry.getProjectId(),
                        entry.getUserId()), ar -> {
                    if (ar.succeeded()) {
                        LOGGER.info("Updated Time Entry by ID {}", entry.getId());
                        resultHandler.handle(Future.succeededFuture(entry));
                    } else {
                        LOGGER.error("Time Entry with id {} not found", entry.getId(), ar.cause());
                        resultHandler.handle(Future.failedFuture(ar.cause()));
                    }
                });
    }

    public void delete(long id, Handler<AsyncResult<TimeEntry>> resultHandler) {
        client.preparedQuery(TimeEntrySQL.DELETE_BY_ID)
                .execute(Tuple.of(id), ar -> {
                    if (ar.succeeded()) {
                        LOGGER.info("Deleted Time Entry by ID {}", id);
                        resultHandler.handle(
                                Future.succeededFuture()
                        );
                    } else {
                        LOGGER.error("Time Entry with id {} not found", id, ar.cause());
                        resultHandler.handle(Future.failedFuture(ar.cause()));
                    }
                });
    }

    public void fetchByUserId(int userId, Handler<AsyncResult<List<TimeEntry>>> resultHandler) {
        client.preparedQuery(TimeEntrySQL.SELECT_BY_USER_ID)
                .execute(Tuple.of(userId), ar -> {
                    if (ar.succeeded()) {
                        LOGGER.info("Fetched Time Entry by User ID {}", userId);
                        List<TimeEntry> entries = new ArrayList<>();
                        for (Row row : ar.result()) {
                            entries.add(mapRowToTimeEntry(row));
                        }
                        resultHandler.handle(Future.succeededFuture(entries));
                    } else {
                        LOGGER.error("Time Entry with id {} not found", userId, ar.cause());
                        resultHandler.handle(Future.failedFuture(ar.cause()));
                    }
                });
    }

    private TimeEntry mapRowToTimeEntry(Row row) {
        return new TimeEntry(
                row.getLong("time_entry_id"),
                row.getLocalDateTime("from_time"),
                row.getLocalDateTime("to_time"),
                row.getInteger("project_id"),
                row.getInteger("user_id")
        );
    }
}
