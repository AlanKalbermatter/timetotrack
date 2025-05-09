package com.timetotrack.timetotrack.dao;

import com.timetotrack.timetotrack.constant.ProjectSQL;
import com.timetotrack.timetotrack.model.Project;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ProjectDao {

    private static final Logger logger = LoggerFactory.getLogger(ProjectDao.class);

    private final Pool client;

    public ProjectDao(Pool client) {
        this.client = client;
    }

    public void fetchAll(Handler<AsyncResult<List<Project>>> resultHandler) {
        client.query(ProjectSQL.SELECT_ALL).execute(ar -> {
            if (ar.succeeded()) {
                List<Project> projects = new ArrayList<>();
                for (Row row : ar.result()) {
                    projects.add(mapRowToProject(row));
                }
                resultHandler.handle(Future.succeededFuture(projects));
            } else {
                logger.error("Failed to fetch projects", ar.cause());
                resultHandler.handle(Future.failedFuture(ar.cause()));
            }
        });
    }

    public void fetchById(int id, Handler<AsyncResult<Project>> resultHandler) {
        client.preparedQuery(ProjectSQL.SELECT_BY_ID)
                .execute(Tuple.of(id), ar -> {
                    if (ar.succeeded()) {
                        if (ar.result().rowCount() > 0) {
                            Project project = mapRowToProject(ar.result().iterator().next());
                            logger.info("Fetched project by ID {}", id);
                            resultHandler.handle(Future.succeededFuture(project));
                        } else {
                            logger.warn("Project with ID {} not found", id);
                            resultHandler.handle(Future.failedFuture("Project not found with id: " + id));
                        }
                    } else {
                        logger.error("Failed to fetch project by ID {}", id, ar.cause());
                        resultHandler.handle(Future.failedFuture(ar.cause()));
                    }
                });
    }

    public void create(Project project, Handler<AsyncResult<Project>> resultHandler) {
        client.preparedQuery(ProjectSQL.INSERT_ONE)
                .execute(Tuple.of(project.getName(), project.getCustomerId()), ar -> {
                    if (ar.succeeded()) {
                        project.setId(ar.result().iterator().next().getInteger("project_id"));
                        logger.info("Created project with ID {}", project.getId());
                        resultHandler.handle(Future.succeededFuture(project));
                    } else {
                        logger.error("Failed to create project", ar.cause());
                        resultHandler.handle(Future.failedFuture(ar.cause()));
                    }
                });
    }

    public void update(Project project, Handler<AsyncResult<Project>> resultHandler) {
        client.preparedQuery(ProjectSQL.UPDATE_ONE)
                .execute(Tuple.of(project.getName(), project.getCustomerId(), project.getId()), ar -> {
                    if (ar.succeeded()) {
                        logger.info("Updated project with ID {}", project.getId());
                        resultHandler.handle(Future.succeededFuture(project));
                    } else {
                        logger.error("Failed to update project with ID {}", project.getId(), ar.cause());
                        resultHandler.handle(Future.failedFuture(ar.cause()));
                    }
                });
    }

    public void delete(int id, Handler<AsyncResult<Void>> resultHandler) {
        client.preparedQuery(ProjectSQL.DELETE_BY_ID)
                .execute(Tuple.of(id), ar -> {
                    if (ar.succeeded()) {
                        logger.info("Deleted project with ID {}", id);
                        resultHandler.handle(Future.succeededFuture());
                    } else {
                        logger.error("Failed to delete project with ID {}", id, ar.cause());
                        resultHandler.handle(Future.failedFuture(ar.cause()));
                    }
                });
    }

    private Project mapRowToProject(Row row) {
        return new Project(
                row.getInteger("project_id"),
                row.getString("project_name"),
                row.getInteger("customer_id")
        );
    }
}
