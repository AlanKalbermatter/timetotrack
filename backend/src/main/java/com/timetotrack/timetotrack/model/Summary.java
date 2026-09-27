package com.timetotrack.timetotrack.model;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;

public record Summary(long totalSeconds, List<ProjectTotal> byProject, List<DayTotal> byDay) {

    public record ProjectTotal(int projectId, String projectName, long seconds) {
    }

    /** {@code date} is YYYY-MM-DD in the requested time zone. */
    public record DayTotal(String date, long seconds) {
    }

    public JsonObject toJson() {
        return new JsonObject()
                .put("totalSeconds", totalSeconds)
                .put("byProject", new JsonArray(byProject.stream()
                        .map(p -> new JsonObject()
                                .put("projectId", p.projectId())
                                .put("projectName", p.projectName())
                                .put("seconds", p.seconds()))
                        .toList()))
                .put("byDay", new JsonArray(byDay.stream()
                        .map(d -> new JsonObject().put("date", d.date()).put("seconds", d.seconds()))
                        .toList()));
    }
}
