package com.timetotrack.timetotrack;

import com.timetotrack.timetotrack.config.AppConfig;
import com.timetotrack.timetotrack.config.ServicePorts;
import com.timetotrack.timetotrack.support.IntegrationTest;
import com.timetotrack.timetotrack.support.Ports;
import com.timetotrack.timetotrack.support.TestDatabase;
import com.timetotrack.timetotrack.support.TestHttp;
import com.timetotrack.timetotrack.support.TestHttp.Response;
import com.timetotrack.timetotrack.support.Uploads;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static com.timetotrack.timetotrack.support.Await.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Deploys the whole application and drives it only through the public gateway, like a client would. */
class ApiFlowTest extends IntegrationTest {

    private static TestHttp gateway;

    @BeforeAll
    static void deployApplication() {
        ServicePorts ports = new ServicePorts(Ports.free(), Ports.free(), Ports.free(), Ports.free(), Ports.free(), Ports.free());
        AppConfig config = new AppConfig("test", Ports.free(), ports, TestDatabase.dbConfig(), "test-secret");
        await(vertx.deployVerticle(new MainVerticle(config)));
        gateway = new TestHttp(config.gatewayPort());
    }

    private static String register(String username) {
        Response response = gateway.post("/api/auth/register", new JsonObject()
                .put("email", username + "@example.com")
                .put("username", username)
                .put("fullName", username.toUpperCase())
                .put("password", "correct-horse"));
        assertEquals(201, response.status(), response.body());
        return response.json().getString("token");
    }

    @Test
    void rejectsRequestsWithoutAValidToken() {
        Response missing = gateway.get("/api/customers");
        assertEquals(401, missing.status());
        assertEquals("Missing bearer token", missing.error());

        assertEquals(401, gateway.withToken("not-a-jwt").get("/api/customers").status());
    }

    @Test
    void docsAreServedWithoutAuthentication() {
        Response spec = gateway.get("/api/docs/openapi.yaml");
        assertEquals(200, spec.status());
        assertTrue(spec.body().startsWith("openapi: 3"));
        assertTrue(gateway.get("/api/docs").body().contains("swagger-ui"));
    }

    @Test
    void unauthenticatedMultipartUploadsAreNeverWrittenToDisk() {
        Uploads.deleteDirectory();

        Response response = gateway.send("POST", "/api/nope", Uploads.CONTENT_TYPE, Uploads.BODY);

        assertEquals(404, response.status());
        assertFalse(Files.exists(Uploads.DIRECTORY), "the gateway wrote an unauthenticated upload to disk");
    }

    @Test
    void unknownRoutesAre404() {
        assertEquals(404, gateway.get("/api/nope").status());
    }

    @Test
    void fullTrackingFlow() {
        TestHttp ana = gateway.withToken(register("ana"));

        Response login = gateway.post("/api/auth/login",
                new JsonObject().put("email", "ana@example.com").put("password", "correct-horse"));
        assertEquals(200, login.status());

        int customerId = ana.post("/api/customers", new JsonObject().put("name", "Acme")).json().getInteger("id");
        int projectId = ana.post("/api/projects", new JsonObject().put("name", "Website").put("customerId", customerId))
                .json().getInteger("id");

        assertEquals(201, ana.post("/api/time-entries/start", new JsonObject().put("projectId", projectId)).status());
        assertEquals(200, ana.get("/api/time-entries/current").status());
        assertEquals(200, ana.post("/api/time-entries/stop", null).status());
        assertEquals(1, ana.get("/api/time-entries").jsonArray().size());

        Instant now = Instant.now();
        Response summary = ana.get("/api/time-entries/summary?from=" + now.minus(1, ChronoUnit.DAYS)
                + "&to=" + now.plus(1, ChronoUnit.DAYS) + "&tz=UTC");
        assertEquals(200, summary.status());
        assertEquals("Website", summary.json().getJsonArray("byProject").getJsonObject(0).getString("projectName"));

        assertEquals("ana", ana.get("/api/users/me").json().getString("username"));
    }

    @Test
    void aSpoofedUserHeaderIsIgnored() {
        TestHttp ana = gateway.withToken(register("ana"));
        int anaId = ana.get("/api/users/me").json().getInteger("id");
        int customerId = ana.post("/api/customers", new JsonObject().put("name", "Acme")).json().getInteger("id");
        int projectId = ana.post("/api/projects", new JsonObject().put("name", "Website").put("customerId", customerId))
                .json().getInteger("id");
        long anasEntry = ana.post("/api/time-entries/start", new JsonObject().put("projectId", projectId)).json().getLong("id");

        TestHttp bobPretendingToBeAna = gateway.withToken(register("bob")).withHeader("X-User-Id", String.valueOf(anaId));

        assertEquals(0, bobPretendingToBeAna.get("/api/time-entries").jsonArray().size());
        assertEquals(204, bobPretendingToBeAna.get("/api/time-entries/current").status());
        assertEquals(404, bobPretendingToBeAna.delete("/api/time-entries/" + anasEntry).status());
        assertEquals("bob", bobPretendingToBeAna.get("/api/users/me").json().getString("username"));
    }
}
