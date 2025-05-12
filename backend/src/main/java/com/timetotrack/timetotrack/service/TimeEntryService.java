package com.timetotrack.timetotrack.service;

import com.timetotrack.timetotrack.dao.TimeEntryDao;
import com.timetotrack.timetotrack.model.Project;
import com.timetotrack.timetotrack.model.TimeEntry;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import javax.inject.Singleton;
import java.util.List;

@Singleton
public class TimeEntryService {
    private final TimeEntryDao dao;

    public TimeEntryService(TimeEntryDao timeEntryDao) {
        this.dao = timeEntryDao;
    }

    public void create(TimeEntry timeEntry, Handler<AsyncResult<TimeEntry>> handler) {
        dao.create(timeEntry, handler);
    }

    public void fetchAll(Handler<AsyncResult<List<TimeEntry>>> handler) {
        dao.fetchAll(handler);
    }

    public void fetchById(long id, Handler<AsyncResult<TimeEntry>> handler) {
        dao.fetchById(id, handler);
    }

    public void update(TimeEntry timeEntry, Handler<AsyncResult<TimeEntry>> handler) {
        dao.update(timeEntry, handler);
    }

    public void delete(long id, Handler<AsyncResult<TimeEntry>> handler) {
        dao.delete(id, handler);
    }

    public void fetchByUserId(int userId, Handler<AsyncResult<List<TimeEntry>>> handler) {
        dao.fetchByUserId(userId, handler);
    }
}
