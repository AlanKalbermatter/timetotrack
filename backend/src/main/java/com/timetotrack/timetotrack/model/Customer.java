package com.timetotrack.timetotrack.model;

import io.vertx.core.json.JsonObject;

public record Customer(Integer id, String name) {

    public JsonObject toJson() {
        return new JsonObject().put("id", id).put("name", name);
    }
}
