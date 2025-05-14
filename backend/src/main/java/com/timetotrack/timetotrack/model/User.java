package com.timetotrack.timetotrack.model;

import io.vertx.core.json.JsonObject;

public class User {
    private Integer id;
    private String username;
    private String email;
    private String fullName;

    public User() {
    }

    public User(Integer id, String username, String email, String fullName) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
    }

    public static User fromJson(JsonObject json) {
        return new User(
                null,
                json.getString("username"),
                json.getString("email"),
                json.getString("full_name")
        );
    }

    public JsonObject toJson() {
        return new JsonObject()
                .put("id", id)
                .put("username", username)
                .put("email", email)
                .put("full_name", fullName);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
