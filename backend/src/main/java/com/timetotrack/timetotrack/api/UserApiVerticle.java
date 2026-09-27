package com.timetotrack.timetotrack.api;

import com.timetotrack.timetotrack.http.Http;
import com.timetotrack.timetotrack.http.ServiceVerticle;
import com.timetotrack.timetotrack.model.User;
import com.timetotrack.timetotrack.service.UserService;
import io.vertx.ext.web.Router;

/** Read-only team directory. Accounts are created through the auth service. */
public class UserApiVerticle extends ServiceVerticle {

    private final UserService users;

    public UserApiVerticle(UserService users, int port) {
        super(port);
        this.users = users;
    }

    @Override
    protected void routes(Router router) {
        router.get("/api/users").handler(ctx -> Http.respond(ctx, 200,
                () -> users.findAll().map(list -> Http.toArray(list, User::toJson))));
        router.get("/api/users/me").handler(ctx -> Http.respond(ctx, 200,
                () -> users.findById(Http.userId(ctx)).map(User::toJson)));
    }
}
