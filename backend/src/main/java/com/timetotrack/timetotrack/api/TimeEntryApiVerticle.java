package com.timetotrack.timetotrack.api;

import com.timetotrack.timetotrack.http.Http;
import com.timetotrack.timetotrack.http.JsonFields;
import com.timetotrack.timetotrack.http.ServiceVerticle;
import com.timetotrack.timetotrack.model.TimeEntry;
import com.timetotrack.timetotrack.service.TimeEntryService;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

/** All routes act on the caller's own entries (X-User-Id). */
public class TimeEntryApiVerticle extends ServiceVerticle {

    private final TimeEntryService entries;

    public TimeEntryApiVerticle(TimeEntryService entries, int port) {
        super(port);
        this.entries = entries;
    }

    @Override
    protected void routes(Router router) {
        router.get("/api/time-entries").handler(ctx -> Http.respond(ctx, 200,
                () -> entries.list(Http.userId(ctx), Http.queryInstant(ctx, "from"), Http.queryInstant(ctx, "to"))
                        .map(list -> Http.toArray(list, TimeEntry::toJson))));
        router.post("/api/time-entries").handler(ctx -> Http.respond(ctx, 201, () -> {
            JsonObject body = Http.body(ctx);
            return entries.createManual(Http.userId(ctx),
                    JsonFields.requiredInt(body, "projectId"),
                    JsonFields.optionalString(body, "description"),
                    JsonFields.requiredInstant(body, "from"),
                    JsonFields.requiredInstant(body, "to")).map(TimeEntry::toJson);
        }));
        router.get("/api/time-entries/current").handler(ctx -> Http.respond(ctx, 200,
                () -> entries.current(Http.userId(ctx)).map(found -> found.map(TimeEntry::toJson).orElse(null))));
        router.post("/api/time-entries/start").handler(ctx -> Http.respond(ctx, 201, () -> {
            JsonObject body = Http.body(ctx);
            return entries.start(Http.userId(ctx),
                    JsonFields.requiredInt(body, "projectId"),
                    JsonFields.optionalString(body, "description")).map(TimeEntry::toJson);
        }));
        router.post("/api/time-entries/stop").handler(ctx -> Http.respond(ctx, 200,
                () -> entries.stop(Http.userId(ctx)).map(TimeEntry::toJson)));
        router.delete("/api/time-entries/:id").handler(ctx -> Http.respond(ctx, 204,
                () -> entries.delete(Http.userId(ctx), Http.pathId(ctx, "id"))));
    }
}
