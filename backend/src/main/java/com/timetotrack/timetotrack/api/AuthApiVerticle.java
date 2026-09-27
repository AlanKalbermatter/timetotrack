package com.timetotrack.timetotrack.api;

import com.timetotrack.timetotrack.auth.AuthResult;
import com.timetotrack.timetotrack.auth.AuthService;
import com.timetotrack.timetotrack.http.Http;
import com.timetotrack.timetotrack.http.JsonFields;
import com.timetotrack.timetotrack.http.ServiceVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

public class AuthApiVerticle extends ServiceVerticle {

    private final AuthService auth;

    public AuthApiVerticle(AuthService auth, int port) {
        super(port);
        this.auth = auth;
    }

    @Override
    protected boolean requiresUser() {
        return false;
    }

    @Override
    protected void routes(Router router) {
        router.post("/api/auth/register").handler(ctx -> Http.respond(ctx, 201, () -> {
            JsonObject body = Http.body(ctx);
            return auth.register(
                    JsonFields.optionalString(body, "email"),
                    JsonFields.optionalString(body, "username"),
                    JsonFields.optionalString(body, "fullName"),
                    JsonFields.optionalRawString(body, "password")).map(AuthResult::toJson);
        }));
        router.post("/api/auth/login").handler(ctx -> Http.respond(ctx, 200, () -> {
            JsonObject body = Http.body(ctx);
            return auth.login(
                    JsonFields.optionalString(body, "email"),
                    JsonFields.optionalRawString(body, "password")).map(AuthResult::toJson);
        }));
    }
}
