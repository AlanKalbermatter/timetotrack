package com.timetotrack.timetotrack.model;

import io.vertx.core.json.JsonObject;

public record User(Integer id, String username, String email, String fullName) {

    public JsonObject toJson() {
        return new JsonObject()
                .put("id", id)
                .put("username", username)
                .put("email", email)
                .put("fullName", fullName);
    }
}
