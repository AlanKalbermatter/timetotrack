package com.timetotrack.timetotrack.service;

import com.timetotrack.timetotrack.dao.ProjectDao;
import com.timetotrack.timetotrack.model.Project;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import javax.inject.Singleton;
import java.util.List;

@Singleton
public class ProjectService {
    private final ProjectDao dao;

    public ProjectService(ProjectDao dao) {
        this.dao = dao;
    }

    public void create(Project project, Handler<AsyncResult<Project>> handler) {
        dao.create(project, handler);
    }

    public void fetchAll(Handler<AsyncResult<List<Project>>> handler) {
        dao.fetchAll(handler);
    }

    public void fetchById(Integer id, Handler<AsyncResult<Project>> handler) {
        dao.fetchById(id, handler);
    }

    public void update(Project project, Handler<AsyncResult<Project>> handler) {
        dao.update(project, handler);
    }

    public void delete(Integer id, Handler<AsyncResult<Void>> handler) {
        dao.delete(id, handler);
    }
}
