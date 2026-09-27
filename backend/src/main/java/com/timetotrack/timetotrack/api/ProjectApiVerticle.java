package com.timetotrack.timetotrack.api;

import com.timetotrack.timetotrack.http.Http;
import com.timetotrack.timetotrack.http.JsonFields;
import com.timetotrack.timetotrack.http.ServiceVerticle;
import com.timetotrack.timetotrack.model.Project;
import com.timetotrack.timetotrack.service.ProjectService;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

/**
 * Routes: {@code GET|POST /api/projects}, {@code GET|PUT|DELETE /api/projects/:id}.
 */
public class ProjectApiVerticle extends ServiceVerticle {

    private final ProjectService projects;

    public ProjectApiVerticle(ProjectService projects, int port) {
        super(port);
        this.projects = projects;
    }

    @Override
    protected void routes(Router router) {
        router.get("/api/projects").handler(ctx -> Http.respond(ctx, 200,
                () -> projects.findAll().map(list -> Http.toArray(list, Project::toJson))));
        router.post("/api/projects").handler(ctx -> Http.respond(ctx, 201, () -> {
            JsonObject body = Http.body(ctx);
            return projects.create(JsonFields.optionalString(body, "name"), JsonFields.requiredInt(body, "customerId"))
                    .map(Project::toJson);
        }));
        router.get("/api/projects/:id").handler(ctx -> Http.respond(ctx, 200,
                () -> projects.findById(Http.pathIntId(ctx, "id")).map(Project::toJson)));
        router.put("/api/projects/:id").handler(ctx -> Http.respond(ctx, 200, () -> {
            JsonObject body = Http.body(ctx);
            return projects.update(Http.pathIntId(ctx, "id"),
                    JsonFields.optionalString(body, "name"),
                    JsonFields.requiredInt(body, "customerId")).map(Project::toJson);
        }));
        router.delete("/api/projects/:id").handler(ctx -> Http.respond(ctx, 204,
                () -> projects.delete(Http.pathIntId(ctx, "id"))));
    }
}
