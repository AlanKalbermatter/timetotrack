package com.timetotrack.timetotrack.api;

import com.timetotrack.timetotrack.model.TimeEntry;
import com.timetotrack.timetotrack.service.TimeEntryService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeEntryApiVerticle
        extends AbstractVerticle
        implements VerticleCRUDCommons {

    private final TimeEntryService timeEntryService;
    private final int port;
    private final Logger LOGGER = LoggerFactory.getLogger(TimeEntryApiVerticle.class);

    public TimeEntryApiVerticle(TimeEntryService timeEntryService, int port) {
        this.timeEntryService = timeEntryService;
        this.port = port;
    }

    @Override
    public void start(Promise<Void> startPromise) {
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        router.post("/api/time_entry").handler(this::handleCreate);
        router.get("/api/time_entry").handler(this::handleFetchAll);
        router.get("/api/time_entry/:id").handler(this::handleFetchById);
        router.put("/api/time_entry/:id").handler(this::handleUpdate);
        router.delete("/api/time_entry/:id").handler(this::handleDelete);
        router.get("/api/users/:id/time-entries").handler(this::handleGetByUserId);

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(port, http -> {
                    if (http.succeeded()) {
                        LOGGER.info("Time Entry API ready at http://localhost:{}/api/time_entry", port);
                        startPromise.complete();
                    } else {
                        LOGGER.error("HTTP server failed to start", http.cause());
                        startPromise.fail(http.cause());
                    }
                });
    }

    @Override
    public void handleCreate(RoutingContext routingContext) {
        TimeEntry timeEntry = routingContext
                .body()
                .asJsonObject()
                .mapTo(TimeEntry.class);
        timeEntryService.create(timeEntry, ar -> {
            if (ar.succeeded()) {
                LOGGER.info("Created time entry with id {}", timeEntry.getId());
                routingContext.response()
                        .putHeader("content-type", "application/json")
                        .setStatusCode(201)
                        .end(JsonObject.mapFrom(ar.result()).encodePrettily());
            } else {
                LOGGER.error(ar.cause().getMessage());
                routingContext.response().setStatusCode(500).end("Error creating time entry: " + ar.cause().getMessage());
            }
        });
    }

    @Override
    public void handleFetchAll(RoutingContext routingContext) {
        timeEntryService.fetchAll(ar -> {
            if (ar.succeeded()) {
                LOGGER.info("Successfully fetched all time entries");
                routingContext.response()
                        .putHeader("content-type", "application/json")
                        .setStatusCode(200)
                        .end(JsonObject.mapFrom(ar.result()).encodePrettily());
            } else {
                LOGGER.error(ar.cause().getMessage());
                routingContext.response().setStatusCode(500).end("Error fetching all time entries: " + ar.cause().getMessage());
            }
        });
    }

    @Override
    public void handleFetchById(RoutingContext routingContext) {
        long id = Long.parseLong(routingContext.request().getParam("id"));
        timeEntryService.fetchById(id, ar -> {
            if (ar.succeeded()) {
                LOGGER.info("Fetch time entry with id {}", id);
                routingContext.response()
                        .putHeader("content-type", "application/json")
                        .setStatusCode(200)
                        .end(JsonObject.mapFrom(ar.result()).encodePrettily());
            } else {
                LOGGER.error(ar.cause().getMessage());
                routingContext.response().setStatusCode(500).end("Error fetching time entry with id " + id);
            }
        });
    }

    @Override
    public void handleUpdate(RoutingContext routingContext) {
        long id = Long.parseLong(routingContext.request().getParam("id"));
        TimeEntry updated = routingContext.body().asJsonObject().mapTo(TimeEntry.class);
        updated.setId(id);
        timeEntryService.update(updated, ar -> {
            if (ar.succeeded()) {
                LOGGER.info("Update time entry with id {}", id);
                routingContext.response()
                        .putHeader("content-type", "application/json")
                        .setStatusCode(200)
                        .end(JsonObject.mapFrom(ar.result()).encodePrettily());
            } else {
                LOGGER.error(ar.cause().getMessage());
                routingContext.response().setStatusCode(500).end("Error updating time entry with id " + id);
            }
        });
    }

    @Override
    public void handleDelete(RoutingContext routingContext) {
        long id = Long.parseLong(routingContext.request().getParam("id"));
        timeEntryService.delete(id, ar -> {
            if (ar.succeeded()) {
                LOGGER.info("Delete time entry with id {}", id);
                routingContext.response()
                        .putHeader("content-type", "application/json")
                        .setStatusCode(200)
                        .end(JsonObject.mapFrom(ar.result()).encodePrettily());
            } else {
                LOGGER.error(ar.cause().getMessage());
                routingContext.response().setStatusCode(500).end("Error deleting time entry with id " + id);
            }
        });
    }

    private void handleGetByUserId(RoutingContext ctx) {
        String idParam = ctx.pathParam("id");
        int userId;
        try {
            userId = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            ctx.response().setStatusCode(400).end("Invalid user ID");
            return;
        }

        timeEntryService.fetchByUserId(userId, ar -> {
            if (ar.succeeded()) {
                LOGGER.info("Fetch time entry for user with id {}", userId);
                ctx.response()
                        .putHeader("Content-Type", "application/json")
                        .end(Json.encodePrettily(ar.result()));
            } else {
                LOGGER.error("Error retrieving time entries for user with id {}", userId);
                ctx.response().setStatusCode(500).end("Error retrieving time entries for user with id " + userId);
            }
        });
    }
}
