package com.timetotrack.timetotrack.api;

import com.timetotrack.timetotrack.model.Project;
import com.timetotrack.timetotrack.service.ProjectService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class ProjectApiVerticle extends AbstractVerticle {
    private final ProjectService projectService;
    private final int port;

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
                        System.out.println("Project API ready at http://localhost:" + port + "/api/projects");
                        startPromise.complete();
                    } else {
                        startPromise.fail(http.cause());
                    }
                });
    }

    private void handleCreate(RoutingContext ctx) {
        Project project = ctx.body().asJsonObject().mapTo(Project.class);
        projectService.create(project, ar -> {
            if (ar.succeeded()) {
                ctx.response()
                        .setStatusCode(201)
                        .putHeader("Content-Type", "application/json")
                        .end(JsonObject.mapFrom(ar.result()).encode());
            } else {
                ctx.response().setStatusCode(500).end("Error creating project");
            }
        });
    }

    private void handleFetchAll(RoutingContext ctx) {
        projectService.fetchAll(ar -> {
            if (ar.succeeded()) {
                ctx.response()
                        .putHeader("Content-Type", "application/json")
                        .end(JsonObject.mapFrom(new JsonObject().put("data", ar.result())).encode());
            } else {
                ctx.response().setStatusCode(500).end("Error fetching projects");
            }
        });
    }

    private void handleFetchById(RoutingContext ctx) {
        Integer id = Integer.parseInt(ctx.pathParam("id"));
        projectService.fetchById(id, ar -> {
            if (ar.succeeded()) {
                ctx.response()
                        .putHeader("Content-Type", "application/json")
                        .end(JsonObject.mapFrom(ar.result()).encode());
            } else {
                ctx.response().setStatusCode(404).end("Project not found");
            }
        });
    }

    private void handleUpdate(RoutingContext ctx) {
        Integer id = Integer.parseInt(ctx.pathParam("id"));
        Project updated = ctx.body().asJsonObject().mapTo(Project.class);
        updated.setId(id);
        projectService.update(updated, ar -> {
            if (ar.succeeded()) {
                ctx.response()
                        .putHeader("Content-Type", "application/json")
                        .end(JsonObject.mapFrom(ar.result()).encode());
            } else {
                ctx.response().setStatusCode(500).end("Error updating project");
            }
        });
    }

    private void handleDelete(RoutingContext ctx) {
        Integer id = Integer.parseInt(ctx.pathParam("id"));
        projectService.delete(id, ar -> {
            if (ar.succeeded()) {
                ctx.response().setStatusCode(204).end();
            } else {
                ctx.response().setStatusCode(500).end("Error deleting project");
            }
        });
    }
}
