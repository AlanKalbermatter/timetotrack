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

public class UserApiVerticle extends AbstractVerticle {

    private final UserService userService;

    public UserApiVerticle(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void start(Promise<Void> startPromise) {
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.get("/api/users").handler(this::handleGetAllUsers);
        router.post("/api/users").handler(this::handleCreateUser);


        vertx.createHttpServer()
                .requestHandler(router)
                .listen(8888, http -> {
                    if (http.succeeded()) {
                        System.out.println("User API ready at http://localhost:8888/api/users");
                        startPromise.complete();
                    } else {
                        startPromise.fail(http.cause());
                    }
                });
    }

    private void handleGetAllUsers(RoutingContext ctx) {
        userService.getAllUsers(res -> {
            if (res.succeeded()) {
                JsonArray jsonUsers = new JsonArray();
                res.result().forEach(u -> jsonUsers.add(u.toJson()));
                ctx.response().putHeader("Content-Type", "application/json").end(jsonUsers.encodePrettily());
            } else {
                ctx.response().setStatusCode(500).end("Error fetching users");
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
                        .putHeader("content-type", "application/json")
                        .end(res.result().toJson().encodePrettily());
            } else {
                res.cause().printStackTrace(); // ver qué falló exactamente
                ctx.response().setStatusCode(500).end("Error creating user");
            }
        });
    }
}

