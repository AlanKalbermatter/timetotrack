package com.timetotrack.timetotrack.http;

import com.timetotrack.timetotrack.error.ValidationException;
import com.timetotrack.timetotrack.support.Ports;
import com.timetotrack.timetotrack.support.TestHttp;
import com.timetotrack.timetotrack.support.TestHttp.Response;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.timetotrack.timetotrack.support.Await.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ServiceVerticleTest {

    /** Minimal service exercising every Http helper path. */
    static final class ProbeVerticle extends ServiceVerticle {

        ProbeVerticle(int port) {
            super(port);
        }

        @Override
        protected void routes(Router router) {
            router.get("/ok").handler(ctx -> Http.respond(ctx, 200,
                    () -> Future.succeededFuture(new JsonObject().put("ok", true).put("user", Http.userId(ctx)))));
            router.post("/echo").handler(ctx -> Http.respond(ctx, 201, () -> Future.succeededFuture(Http.body(ctx))));
            router.get("/none").handler(ctx -> Http.respond(ctx, 200, () -> Future.succeededFuture(null)));
            router.get("/invalid").handler(ctx -> Http.respond(ctx, 200, () -> {
                throw new ValidationException("bad input");
            }));
            router.get("/boom").handler(ctx -> Http.respond(ctx, 200,
                    () -> Future.failedFuture(new IllegalStateException("secret detail"))));
            router.get("/items/:id").handler(ctx -> Http.respond(ctx, 200,
                    () -> Future.succeededFuture(new JsonObject().put("id", Http.pathId(ctx, "id")))));
        }
    }

    private static Vertx vertx;
    private static TestHttp anonymous;
    private static TestHttp http;

    @BeforeAll
    static void deploy() {
        vertx = Vertx.vertx();
        int port = Ports.free();
        await(vertx.deployVerticle(new ProbeVerticle(port)));
        anonymous = new TestHttp(port);
        http = anonymous.asUser(7);
    }

    @AfterAll
    static void close() {
        await(vertx.close());
    }

    @Test
    void respondsWithJsonAndReadsUserHeader() {
        Response response = http.get("/ok");

        assertEquals(200, response.status());
        assertEquals(new JsonObject().put("ok", true).put("user", 7), response.json());
    }

    @Test
    void requiresUserHeader() {
        assertEquals(401, anonymous.get("/ok").status());
        assertEquals(401, anonymous.withHeader("X-User-Id", "abc").get("/ok").status());
        assertEquals("Missing authenticated user", anonymous.get("/ok").error());
    }

    @Test
    void echoesJsonBodies() {
        Response response = http.post("/echo", new JsonObject().put("a", 1));

        assertEquals(201, response.status());
        assertEquals(new JsonObject().put("a", 1), response.json());
    }

    @Test
    void rejectsMalformedEmptyAndArrayBodies() {
        assertEquals(400, http.send("POST", "/echo", "{not json").status());
        assertEquals(400, http.send("POST", "/echo", "").status());
        assertEquals(400, http.send("POST", "/echo", "[1,2]").status());
        assertEquals("Request body must be a JSON object", http.send("POST", "/echo", "[1,2]").error());
    }

    @Test
    void nullResultIsNoContent() {
        assertEquals(204, http.get("/none").status());
    }

    @Test
    void apiExceptionsKeepTheirStatusAndMessage() {
        Response response = http.get("/invalid");

        assertEquals(400, response.status());
        assertEquals("bad input", response.error());
    }

    @Test
    void unexpectedFailuresAreOpaque500s() {
        Response response = http.get("/boom");

        assertEquals(500, response.status());
        assertEquals("Internal server error", response.error());
        assertFalse(response.body().contains("secret"));
    }

    @Test
    void rejectsNonNumericAndNonPositiveIds() {
        assertEquals(200, http.get("/items/12").status());
        assertEquals(400, http.get("/items/abc").status());
        assertEquals(400, http.get("/items/0").status());
    }

    @Test
    void unknownRoutesAreJson404s() {
        Response response = http.get("/nope");

        assertEquals(404, response.status());
        assertEquals("No route for GET /nope", response.error());
    }
}
