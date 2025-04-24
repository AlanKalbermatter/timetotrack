package com.timetotrack.timetotrack.api;

import com.timetotrack.timetotrack.service.UserService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class UserApiVerticle extends AbstractVerticle {

    private final UserService userService;

    public UserApiVerticle(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void start(Promise<Void> startPromise) {
        Router router = Router.router(vertx);
        router.get("/api/users").handler(this::handleGetAllUsers);

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
}

