package com.timetotrack.timetotrack.model;

import io.vertx.core.json.JsonObject;

public record Project(Integer id, String name, Integer customerId, String customerName) {

    public JsonObject toJson() {
        return new JsonObject()
                .put("id", id)
                .put("name", name)
                .put("customerId", customerId)
                .put("customerName", customerName);
    }
}
