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

public class UserApiVerticle extends AbstractVerticle {

    private final UserService userService;
    private final int port;
    private static final Logger log = LoggerFactory.getLogger(UserApiVerticle.class);

    public UserApiVerticle(UserService userService, int port) {
        this.userService = userService;
        this.port = port;
    }

    @Override
    public void start(Promise<Void> startPromise) {
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        router.get("/api/users").handler(this::handleGetAllUsers);
        router.get("/api/users/:id").handler(this::handleGetUserById);
        router.post("/api/users").handler(this::handleCreateUser);
        router.put("/api/users/:id").handler(this::handleUpdateUser);

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(port, http -> {
                    if (http.succeeded()) {
                        log.info("User API ready at http://localhost:{}/api/users", port);
                        startPromise.complete();
                    } else {
                        log.error("Failed to start User API", http.cause());
                        startPromise.fail(http.cause());
                    }
                });
    }

    private void handleGetAllUsers(RoutingContext ctx) {
        userService.getAllUsers(res -> {
            if (res.succeeded()) {
                List<User> users = res.result();
                JsonArray jsonUsers = new JsonArray();
                users.forEach(user -> jsonUsers.add(user.toJson()));
                ctx.response()
                        .putHeader("Content-Type", "application/json")
                        .end(jsonUsers.encodePrettily());
            } else {
                log.error("Failed to fetch users", res.cause());
                ctx.response().setStatusCode(500).end("Error fetching users");
            }
        });
    }

    private void handleGetUserById(RoutingContext ctx) {
        String idParam = ctx.pathParam("id");
        long id;
        try {
            id = Long.parseLong(idParam);
        } catch (NumberFormatException e) {
            ctx.response().setStatusCode(400).end("Invalid user ID");
            return;
        }

        userService.getUserById(id, ar -> {
            if (ar.succeeded()) {
                User user = ar.result();
                if (user != null) {
                    ctx.response()
                            .putHeader("Content-Type", "application/json")
                            .end(user.toJson().encodePrettily());
                } else {
                    ctx.response().setStatusCode(404).end("User not found");
                }
            } else {
                log.error("Error retrieving user by ID {}", id, ar.cause());
                ctx.response().setStatusCode(500).end("Failed to retrieve user");
            }
        });
    }

    private void handleCreateUser(RoutingContext ctx) {
        JsonObject body = ctx.getBodyAsJson();
        if (body == null ||
                body.getString("username") == null ||
                body.getString("email") == null ||
                body.getString("full_name") == null) {
            ctx.response().setStatusCode(400).end("Invalid input");
            return;
        }

        User user = User.fromJson(body);
        userService.createUser(user, res -> {
            if (res.succeeded() && res.result() != null) {
                ctx.response()
                        .setStatusCode(201)
                        .putHeader("Content-Type", "application/json")
                        .end(res.result().toJson().encodePrettily());
            } else {
                log.error("Error creating user", res.cause());
                ctx.response().setStatusCode(500).end("Error creating user");
            }
        });
    }

    private void handleUpdateUser(RoutingContext ctx) {
        String idParam = ctx.pathParam("id");
        long id;
        try {
            id = Long.parseLong(idParam);
        } catch (NumberFormatException e) {
            ctx.response().setStatusCode(400).end("Invalid user ID");
            return;
        }

        JsonObject body = ctx.body().asJsonObject();
        User updatedUser = body.mapTo(User.class);
        updatedUser.setId((int) id);

        userService.updateUser(updatedUser, ar -> {
            if (ar.succeeded()) {
                ctx.response()
                        .setStatusCode(200)
                        .putHeader("Content-Type", "application/json")
                        .end(ar.result().toJson().encodePrettily());
            } else {
                log.error("Error updating user with ID {}", id, ar.cause());
                ctx.response().setStatusCode(500).end("Error updating user");
            }
        });
    }
}
