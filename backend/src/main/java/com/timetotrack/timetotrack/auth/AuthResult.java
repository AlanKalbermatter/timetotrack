package com.timetotrack.timetotrack.auth;

import com.timetotrack.timetotrack.model.User;
import io.vertx.core.json.JsonObject;

/**
 * A successful registration or login: the JWT to send as {@code Authorization: Bearer} plus the user profile.
 */
public record AuthResult(String token, User user) {

    public JsonObject toJson() {
        return new JsonObject().put("token", token).put("user", user.toJson());
    }
}
