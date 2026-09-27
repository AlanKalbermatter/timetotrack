package com.timetotrack.timetotrack.model;

import io.vertx.core.json.JsonObject;

/**
 * A team member's public profile. Never carries the password hash.
 */
public record User(Integer id, String username, String email, String fullName) {

    public JsonObject toJson() {
        return new JsonObject()
                .put("id", id)
                .put("username", username)
                .put("email", email)
                .put("fullName", fullName);
    }
}
