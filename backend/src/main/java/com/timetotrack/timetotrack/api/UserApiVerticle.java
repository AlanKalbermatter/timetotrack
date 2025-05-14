package com.timetotrack.timetotrack.api;

import com.timetotrack.timetotrack.model.User;
import com.timetotrack.timetotrack.service.UserService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UserApiVerticle
        extends AbstractVerticle
        implements VerticleCRUDCommons {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserApiVerticle.class);
    private final UserService userService;
    private final int port;

    public UserApiVerticle(UserService userService, int port) {
        this.userService = userService;
        this.port = port;
    }

    @Override
    public void start(Promise<Void> startPromise) {
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        router.get("/api/users").handler(this::handleFetchAll);
        router.get("/api/users/:id").handler(this::handleFetchById);
        router.post("/api/users").handler(this::handleCreate);
        router.put("/api/users/:id").handler(this::handleUpdate);
        router.delete("/api/users/:id").handler(this::handleDelete);

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(port, http -> {
                    if (http.succeeded()) {
                        LOGGER.info("User API ready at http://localhost:{}/api/users", port);
                        startPromise.complete();
                    } else {
                        LOGGER.error("Failed to start User API", http.cause());
                        startPromise.fail(http.cause());
                    }
                });
    }

    @Override
    public void handleFetchAll(RoutingContext routingContext) {
        userService.getAllUsers(res -> {
            if (res.succeeded()) {
                LOGGER.info("Successfully fetched all users");
                List<User> users = res.result();
                JsonArray jsonUsers = new JsonArray();
                users.forEach(user -> jsonUsers.add(user.toJson()));
                routingContext.response()
                        .putHeader("Content-Type", "application/json")
                        .end(jsonUsers.encodePrettily());
            } else {
                LOGGER.error("Failed to fetch all users", res.cause());
                routingContext.response().setStatusCode(500).end("Error fetching users");
            }
        });
    }

    @Override
    public void handleFetchById(RoutingContext routingContext) {
        String idParam = routingContext.pathParam("id");
        long id;
        try {
            id = Long.parseLong(idParam);
        } catch (NumberFormatException e) {
            routingContext.response().setStatusCode(400).end("Invalid user ID");
            return;
        }

        userService.getUserById(id, ar -> {
            if (ar.succeeded()) {
                LOGGER.info("Successfully fetched user with id {}", id);
                User user = ar.result();
                if (user != null) {
                    routingContext.response()
                            .putHeader("Content-Type", "application/json")
                            .end(user.toJson().encodePrettily());
                } else {
                    routingContext.response().setStatusCode(404).end("User not found");
                }
            } else {
                LOGGER.error("Error retrieving user by ID {}", id, ar.cause());
                routingContext.response().setStatusCode(500).end("Failed to retrieve user");
            }
        });
    }

    @Override
    public void handleCreate(RoutingContext routingContext) {
        JsonObject body = routingContext.getBodyAsJson();
        if (body == null ||
                body.getString("username") == null ||
                body.getString("email") == null ||
                body.getString("full_name") == null) {
            routingContext.response().setStatusCode(400).end("Invalid input");
            return;
        }

        User user = User.fromJson(body);
        userService.createUser(user, res -> {
            if (res.succeeded() && res.result() != null) {
                LOGGER.info("Successfully created user {}", user.getId());
                routingContext.response()
                        .setStatusCode(201)
                        .putHeader("Content-Type", "application/json")
                        .end(res.result().toJson().encodePrettily());
            } else {
                LOGGER.error("Error creating user", res.cause());
                routingContext.response().setStatusCode(500).end("Error creating user");
            }
        });
    }

    @Override
    public void handleUpdate(RoutingContext routingContext) {
        String idParam = routingContext.pathParam("id");
        long id;
        try {
            id = Long.parseLong(idParam);
        } catch (NumberFormatException e) {
            routingContext.response().setStatusCode(400).end("Invalid user ID");
            return;
        }

        JsonObject body = routingContext.body().asJsonObject();
        User updatedUser = body.mapTo(User.class);
        updatedUser.setId((int) id);

        userService.updateUser(updatedUser, ar -> {
            if (ar.succeeded()) {
                LOGGER.info("Successfully updated user {}", updatedUser.getId());
                routingContext.response()
                        .setStatusCode(200)
                        .putHeader("Content-Type", "application/json")
                        .end(ar.result().toJson().encodePrettily());
            } else {
                LOGGER.error("Error updating user with ID {}", id, ar.cause());
                routingContext.response().setStatusCode(500).end("Error updating user");
            }
        });
    }

    public void handleDelete(RoutingContext routingContext) {
        long id = Long.parseLong(routingContext.pathParam("id"));
        userService.deleteUser(id, ar -> {
            if (ar.succeeded()) {
                LOGGER.info("User with ID {} deleted", id);
                routingContext.response()
                        .putHeader("Content-Type", "application/json")
                        .setStatusCode(200)
                        .end(JsonObject.mapFrom(ar.result()).encodePrettily());
            } else {
                LOGGER.error("Error deleting user with ID {}", id, ar.cause());
                routingContext.response().setStatusCode(500).end("Error deleting user with ID " + id);
            }
        });
    }
}
