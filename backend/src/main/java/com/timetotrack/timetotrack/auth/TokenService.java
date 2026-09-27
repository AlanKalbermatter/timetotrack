package com.timetotrack.timetotrack.auth;

import com.timetotrack.timetotrack.error.UnauthorizedException;
import com.timetotrack.timetotrack.model.User;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.authentication.TokenCredentials;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;

/** Issues and verifies HS256 JWTs whose "sub" claim is the user id. */
public class TokenService {

    private static final String ALGORITHM = "HS256";
    private static final int EXPIRES_IN_MINUTES = 8 * 60;

    private final JWTAuth jwtAuth;

    public TokenService(Vertx vertx, String secret) {
        this.jwtAuth = JWTAuth.create(vertx, new JWTAuthOptions()
                .addPubSecKey(new PubSecKeyOptions().setAlgorithm(ALGORITHM).setBuffer(secret)));
    }

    public String issue(User user) {
        JsonObject claims = new JsonObject()
                .put("sub", String.valueOf(user.id()))
                .put("username", user.username());
        return jwtAuth.generateToken(claims, new JWTOptions()
                .setAlgorithm(ALGORITHM)
                .setExpiresInMinutes(EXPIRES_IN_MINUTES));
    }

    public Future<Integer> verify(String token) {
        return jwtAuth.authenticate(new TokenCredentials(token))
                .map(user -> Integer.parseInt(user.principal().getString("sub")))
                .recover(failure -> Future.failedFuture(new UnauthorizedException("Invalid or expired token")));
    }
}
