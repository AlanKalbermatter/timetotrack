package com.timetotrack.timetotrack.api;

import com.timetotrack.timetotrack.dao.ProjectDao;
import com.timetotrack.timetotrack.service.ProjectService;
import com.timetotrack.timetotrack.support.IntegrationTest;
import com.timetotrack.timetotrack.support.Ports;
import com.timetotrack.timetotrack.support.TestHttp;
import com.timetotrack.timetotrack.support.TestHttp.Response;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static com.timetotrack.timetotrack.support.Await.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ProjectApiVerticleTest extends IntegrationTest {

    private static TestHttp http;

    @BeforeAll
    static void deploy() {
        int port = Ports.free();
        await(vertx.deployVerticle(new ProjectApiVerticle(new ProjectService(new ProjectDao(pool)), port)));
        http = new TestHttp(port).asUser(1);
    }

    private static JsonObject project(String name, Object customerId) {
        return new JsonObject().put("name", name).put("customerId", customerId);
    }

    @Test
    void createReturnsTheProjectWithItsCustomerName() {
        int acme = fixtures.customer("Acme");

        Response response = http.post("/api/projects", project("Website", acme));

        assertEquals(201, response.status());
        assertEquals("Website", response.json().getString("name"));
        assertEquals(acme, response.json().getInteger("customerId"));
        assertEquals("Acme", response.json().getString("customerName"));
    }

    @Test
    void unknownCustomerIs400() {
        Response response = http.post("/api/projects", project("Website", 999));

        assertEquals(400, response.status());
        assertEquals("Customer 999 does not exist", response.error());
    }

    @Test
    void customerIdAsStringIs400() {
        Response response = http.post("/api/projects", project("Website", "3"));

        assertEquals(400, response.status());
        assertEquals("customerId must be an integer", response.error());
    }

    @Test
    void namesAreUniquePerCustomerOnly() {
        int acme = fixtures.customer("Acme");
        int globex = fixtures.customer("Globex");
        fixtures.project(acme, "Website");

        assertEquals(409, http.post("/api/projects", project("Website", acme)).status());
        assertEquals(201, http.post("/api/projects", project("Website", globex)).status());
    }

    @Test
    void updateCanMoveAProjectToAnotherCustomer() {
        int acme = fixtures.customer("Acme");
        int globex = fixtures.customer("Globex");
        int website = fixtures.project(acme, "Website");

        Response response = http.put("/api/projects/" + website, project("Portal", globex));

        assertEquals(200, response.status());
        assertEquals("Portal", response.json().getString("name"));
        assertEquals("Globex", response.json().getString("customerName"));
    }

    @Test
    void listIsOrderedByCustomerThenName() {
        int globex = fixtures.customer("Globex");
        int acme = fixtures.customer("Acme");
        fixtures.project(globex, "Alpha");
        fixtures.project(acme, "Zeta");
        fixtures.project(acme, "Beta");

        List<String> names = http.get("/api/projects").jsonArray().stream()
                .map(p -> ((JsonObject) p).getString("name"))
                .toList();

        assertEquals(List.of("Beta", "Zeta", "Alpha"), names);
    }

    @Test
    void projectsWithTimeEntriesCannotBeDeleted() {
        int projectId = fixtures.project(fixtures.customer("Acme"), "Website");
        fixtures.entry(fixtures.user("ana"), projectId,
                Instant.parse("2026-03-10T09:00:00Z"), Instant.parse("2026-03-10T10:00:00Z"));

        Response response = http.delete("/api/projects/" + projectId);

        assertEquals(409, response.status());
        assertEquals("Project " + projectId + " has time entries", response.error());
        assertEquals(204, http.delete("/api/projects/" + fixtures.project(fixtures.customer("Globex"), "Empty")).status());
    }
}
