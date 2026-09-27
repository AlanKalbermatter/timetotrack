package com.timetotrack.timetotrack.api;

import com.timetotrack.timetotrack.dao.TimeEntryDao;
import com.timetotrack.timetotrack.service.TimeEntryService;
import com.timetotrack.timetotrack.support.IntegrationTest;
import com.timetotrack.timetotrack.support.Ports;
import com.timetotrack.timetotrack.support.TestHttp;
import com.timetotrack.timetotrack.support.TestHttp.Response;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

import static com.timetotrack.timetotrack.support.Await.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TimeEntryApiVerticleTest extends IntegrationTest {

    private static final Instant DAY1_09 = Instant.parse("2026-03-09T09:00:00Z");
    private static final Instant DAY1_10 = Instant.parse("2026-03-09T10:00:00Z");
    private static final Instant DAY2_09 = Instant.parse("2026-03-10T09:00:00Z");
    private static final Instant DAY2_11 = Instant.parse("2026-03-10T11:00:00Z");

    private static TestHttp anonymous;
    private int ana;
    private int bob;
    private int website;
    private TestHttp asAna;
    private TestHttp asBob;

    @BeforeAll
    static void deploy() {
        int port = Ports.free();
        TimeEntryService service = new TimeEntryService(new TimeEntryDao(pool), Clock.systemUTC());
        await(vertx.deployVerticle(new TimeEntryApiVerticle(service, port)));
        anonymous = new TestHttp(port);
    }

    @BeforeEach
    void arrange() {
        ana = fixtures.user("ana");
        bob = fixtures.user("bob");
        website = fixtures.project(fixtures.customer("Acme"), "Website");
        asAna = anonymous.asUser(ana);
        asBob = anonymous.asUser(bob);
    }

    @Test
    void timerLifecycle() {
        Response started = asAna.post("/api/time-entries/start",
                new JsonObject().put("projectId", website).put("description", "  Landing page "));
        assertEquals(201, started.status());
        assertNull(started.json().getValue("to"));
        assertEquals("Website", started.json().getString("projectName"));
        assertEquals("Landing page", started.json().getString("description"));
        long id = started.json().getLong("id");

        assertEquals(id, asAna.get("/api/time-entries/current").json().getLong("id"));

        Response second = asAna.post("/api/time-entries/start", new JsonObject().put("projectId", website));
        assertEquals(409, second.status());
        assertEquals("A timer is already running", second.error());

        Response stopped = asAna.post("/api/time-entries/stop", null);
        assertEquals(200, stopped.status());
        assertEquals(id, stopped.json().getLong("id"));
        Instant from = Instant.parse(stopped.json().getString("from"));
        Instant to = Instant.parse(stopped.json().getString("to"));
        assertTrue(to.isAfter(from));

        assertEquals(204, asAna.get("/api/time-entries/current").status());
        Response stopAgain = asAna.post("/api/time-entries/stop", null);
        assertEquals(409, stopAgain.status());
        assertEquals("No timer is running", stopAgain.error());
    }

    @Test
    void timersAreIndependentPerUser() {
        assertEquals(201, asAna.post("/api/time-entries/start", new JsonObject().put("projectId", website)).status());
        assertEquals(201, asBob.post("/api/time-entries/start", new JsonObject().put("projectId", website)).status());
    }

    @Test
    void startingOnAnUnknownProjectIs400() {
        Response response = asAna.post("/api/time-entries/start", new JsonObject().put("projectId", 999));

        assertEquals(400, response.status());
        assertEquals("Project 999 does not exist", response.error());
    }

    @Test
    void manualEntriesMustEndAfterTheyStart() {
        JsonObject body = new JsonObject()
                .put("projectId", website)
                .put("from", DAY1_10.toString())
                .put("to", DAY1_09.toString());

        Response response = asAna.post("/api/time-entries", body);

        assertEquals(400, response.status());
        assertEquals("to must be after from", response.error());
    }

    @Test
    void createsManualEntries() {
        Response response = asAna.post("/api/time-entries", new JsonObject()
                .put("projectId", website)
                .put("description", "Planning")
                .put("from", "2026-03-09T06:00:00-03:00")
                .put("to", DAY1_10.toString()));

        assertEquals(201, response.status());
        assertEquals(DAY1_09.toString(), response.json().getString("from"));
        assertEquals(DAY1_10.toString(), response.json().getString("to"));
        assertEquals("Planning", response.json().getString("description"));
    }

    @Test
    void listReturnsOnlyTheCallersEntriesNewestFirstWithinRange() {
        long older = fixtures.entry(ana, website, DAY1_09, DAY1_10);
        long newer = fixtures.entry(ana, website, DAY2_09, DAY2_11);
        fixtures.entry(bob, website, DAY2_09, DAY2_11);

        Response all = asAna.get("/api/time-entries?from=2026-03-01T00:00:00Z&to=2026-03-31T00:00:00Z");
        List<Long> ids = all.jsonArray().stream().map(e -> ((JsonObject) e).getLong("id")).toList();
        assertEquals(List.of(newer, older), ids);

        Response secondDayOnly = asAna.get("/api/time-entries?from=2026-03-10T00:00:00Z&to=2026-03-11T00:00:00Z");
        assertEquals(1, secondDayOnly.jsonArray().size());
    }

    @Test
    void invalidRangesAre400() {
        assertEquals(400, asAna.get("/api/time-entries?from=yesterday").status());
        assertEquals(400, asAna.get("/api/time-entries?from=2026-03-10T00:00:00Z&to=2026-03-09T00:00:00Z").status());
    }

    @Test
    void usersCannotDeleteEachOthersEntries() {
        long anasEntry = fixtures.entry(ana, website, DAY1_09, DAY1_10);

        assertEquals(404, asBob.delete("/api/time-entries/" + anasEntry).status());
        assertEquals(1, asAna.get("/api/time-entries?from=2026-03-01T00:00:00Z&to=2026-03-31T00:00:00Z").jsonArray().size());

        assertEquals(204, asAna.delete("/api/time-entries/" + anasEntry).status());
        assertEquals(404, asAna.delete("/api/time-entries/" + anasEntry).status());
    }

    @Test
    void requiresTheGatewayUserHeader() {
        assertEquals(401, anonymous.get("/api/time-entries").status());
        assertNotNull(anonymous.get("/api/time-entries").error());
    }
}
