package com.timetotrack.timetotrack.model;

import io.vertx.core.json.JsonObject;

import java.time.Instant;

/** A block of tracked time. {@code to == null} means the timer is still running. */
public record TimeEntry(Long id, int userId, int projectId, String projectName, String description, Instant from, Instant to) {

    public JsonObject toJson() {
        return new JsonObject()
                .put("id", id)
                .put("projectId", projectId)
                .put("projectName", projectName)
                .put("description", description)
                .put("from", from.toString())
                .put("to", to == null ? null : to.toString());
    }
}
