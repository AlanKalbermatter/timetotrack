package com.timetotrack.timetotrack.api;

import com.timetotrack.timetotrack.VerticleCRUDCommons;
import com.timetotrack.timetotrack.model.Project;
import com.timetotrack.timetotrack.service.ProjectService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProjectApiVerticle
        extends AbstractVerticle
        implements VerticleCRUDCommons {
    private final ProjectService projectService;
    private final int port;
    private final Logger LOGGER = LoggerFactory.getLogger(ProjectApiVerticle.class);

    public ProjectApiVerticle(ProjectService service, int port) {
        this.projectService = service;
        this.port = port;
    }

    @Override
    public void start(Promise<Void> startPromise) {
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        router.post("/api/projects").handler(this::handleCreate);
        router.get("/api/projects").handler(this::handleFetchAll);
        router.get("/api/projects/:id").handler(this::handleFetchById);
        router.put("/api/projects/:id").handler(this::handleUpdate);
        router.delete("/api/projects/:id").handler(this::handleDelete);

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(port, http -> {
                    if (http.succeeded()) {
                        LOGGER.info("Project API ready at http://localhost:{}/api/projects", port);
                        startPromise.complete();
                    } else {
                        LOGGER.error("Project API failed to start", http.cause());
                        startPromise.fail(http.cause());
                    }
                });
    }

    @Override
    public void handleCreate(RoutingContext ctx) {
        Project project = ctx.body().asJsonObject().mapTo(Project.class);
        projectService.create(project, ar -> {
            if (ar.succeeded()) {
                LOGGER.info("Created new project with id: {}", project.getId());
                ctx.response()
                        .setStatusCode(201)
                        .putHeader("Content-Type", "application/json")
                        .end(JsonObject.mapFrom(ar.result()).encodePrettily());
            } else {
                LOGGER.error("Failed to create new project", ar.cause());
                ctx.response().setStatusCode(500).end("Error creating project: " + ar.cause().getMessage());
            }
        });
    }

    @Override
    public void handleFetchAll(RoutingContext ctx) {
        projectService.fetchAll(ar -> {
            if (ar.succeeded()) {
                LOGGER.info("Fetched all projects");
                ctx.response()
                        .putHeader("Content-Type", "application/json")
                        .setStatusCode(200)
                        .end(JsonObject.mapFrom(new JsonObject().put("data", ar.result())).encodePrettily());
            } else {
                LOGGER.error("Failed to fetch all projects", ar.cause());
                ctx.response().setStatusCode(500).end("Error fetching projects");
            }
        });
    }

    @Override
    public void handleFetchById(RoutingContext ctx) {
        Integer id = Integer.parseInt(ctx.pathParam("id"));
        projectService.fetchById(id, ar -> {
            if (ar.succeeded()) {
                LOGGER.info("Fetched project with id: {}", id);
                ctx.response()
                        .putHeader("Content-Type", "application/json")
                        .setStatusCode(200)
                        .end(JsonObject.mapFrom(ar.result()).encodePrettily());
            } else {
                LOGGER.error("Failed to fetch project by id: {}", id, ar.cause());
                ctx.response().setStatusCode(404).end("Project not found");
            }
        });
    }

    @Override
    public void handleUpdate(RoutingContext ctx) {
        Integer id = Integer.parseInt(ctx.pathParam("id"));
        Project updated = ctx.body().asJsonObject().mapTo(Project.class);
        updated.setId(id);
        projectService.update(updated, ar -> {
            if (ar.succeeded()) {
                LOGGER.info("Updated project with id: {}", id);
                ctx.response()
                        .putHeader("Content-Type", "application/json")
                        .end(JsonObject.mapFrom(ar.result()).encodePrettily());
            } else {
                LOGGER.error("Failed to update project by id: {}", id, ar.cause());
                ctx.response().setStatusCode(500).end("Error updating project");
            }
        });
    }

    @Override
    public void handleDelete(RoutingContext ctx) {
        Integer id = Integer.parseInt(ctx.pathParam("id"));
        projectService.delete(id, ar -> {
            if (ar.succeeded()) {
                LOGGER.info("Deleted project with id: {}", id);
                ctx.response().setStatusCode(204).end();
            } else {
                LOGGER.error("Failed to delete project by id: {}", id, ar.cause());
                ctx.response().setStatusCode(500).end("Error deleting project");
            }
        });
    }
}
