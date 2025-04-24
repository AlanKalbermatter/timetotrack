package com.timetotrack.timetotrack.model;

import io.vertx.core.json.JsonObject;

public class User {
    public Integer id;
    public String username;
    public String email;
    public String fullName;

    public User(Integer id, String username, String email, String fullName) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
    }

    public JsonObject toJson() {
        return new JsonObject()
                .put("id", id)
                .put("username", username)
                .put("email", email)
                .put("full_name", fullName);
    }
}
