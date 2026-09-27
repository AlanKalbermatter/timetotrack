package com.timetotrack.timetotrack.api;

import com.timetotrack.timetotrack.dao.TimeEntryDao;
import com.timetotrack.timetotrack.service.TimeEntryService;
import com.timetotrack.timetotrack.support.IntegrationTest;
import com.timetotrack.timetotrack.support.Ports;
import com.timetotrack.timetotrack.support.TestHttp;
import com.timetotrack.timetotrack.support.TestHttp.Response;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static com.timetotrack.timetotrack.support.Await.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TimeEntrySummaryTest extends IntegrationTest {

    private static final Instant NOW = Instant.parse("2026-03-10T12:00:00Z");
    private static TestHttp anonymous;

    private TestHttp asAna;
    private int web;
    private int api;
    private int ana;

    @BeforeAll
    static void deploy() {
        int port = Ports.free();
        TimeEntryService service = new TimeEntryService(new TimeEntryDao(pool), Clock.fixed(NOW, ZoneOffset.UTC));
        await(vertx.deployVerticle(new TimeEntryApiVerticle(service, port)));
        anonymous = new TestHttp(port);
    }

    @BeforeEach
    void arrange() {
        ana = fixtures.user("ana");
        int bob = fixtures.user("bob");
        int acme = fixtures.customer("Acme");
        web = fixtures.project(acme, "Web");
        api = fixtures.project(acme, "Api");
        asAna = anonymous.asUser(ana);

        fixtures.entry(ana, web, at("2026-03-09T09:00:00Z"), at("2026-03-09T10:00:00Z"));   // 3600 on day 9
        fixtures.entry(ana, web, at("2026-03-10T08:00:00Z"), at("2026-03-10T09:30:00Z"));   // 5400 on day 10
        fixtures.entry(ana, api, at("2026-03-10T10:00:00Z"), null);                        // running: 7200 until NOW
        fixtures.entry(bob, web, at("2026-03-10T08:00:00Z"), at("2026-03-10T09:00:00Z"));   // someone else's
    }

    private static Instant at(String iso) {
        return Instant.parse(iso);
    }

    private Response summary(String query) {
        return asAna.get("/api/time-entries/summary?" + query);
    }

    @Test
    void totalsByProjectAndDayIncludingTheRunningTimer() {
        Response response = summary("from=2026-03-09T00:00:00Z&to=2026-03-11T00:00:00Z&tz=UTC");

        assertEquals(200, response.status());
        JsonObject body = response.json();
        assertEquals(16200, body.getLong("totalSeconds"));
        assertEquals(new JsonArray()
                .add(new JsonObject().put("projectId", web).put("projectName", "Web").put("seconds", 9000))
                .add(new JsonObject().put("projectId", api).put("projectName", "Api").put("seconds", 7200)),
                body.getJsonArray("byProject"));
        assertEquals(new JsonArray()
                .add(new JsonObject().put("date", "2026-03-09").put("seconds", 3600))
                .add(new JsonObject().put("date", "2026-03-10").put("seconds", 12600)),
                body.getJsonArray("byDay"));
    }

    @Test
    void clipsEntriesToTheRequestedRange() {
        Response response = summary("from=2026-03-09T09:30:00Z&to=2026-03-10T08:30:00Z");

        assertEquals(1800 + 1800, response.json().getLong("totalSeconds"));
    }

    @Test
    void bucketsDaysInTheRequestedTimeZone() {
        fixtures.entry(ana, web, at("2026-03-10T01:00:00Z"), at("2026-03-10T02:00:00Z")); // 22:00 on the 9th in Buenos Aires

        Response response = summary("from=2026-03-10T00:30:00Z&to=2026-03-10T02:30:00Z&tz=America/Argentina/Buenos_Aires");

        assertEquals(new JsonArray().add(new JsonObject().put("date", "2026-03-09").put("seconds", 3600)),
                response.json().getJsonArray("byDay"));
    }

    @Test
    void rejectsInvalidBoundsAndTimeZones() {
        assertEquals("from and to are required", summary("to=2026-03-11T00:00:00Z").error());
        assertEquals("from must be before to", summary("from=2026-03-11T00:00:00Z&to=2026-03-10T00:00:00Z").error());
        assertEquals(400, summary("from=2025-01-01T00:00:00Z&to=2026-03-11T00:00:00Z").status());
        assertEquals(400, summary("from=2026-03-09T00:00:00Z&to=2026-03-11T00:00:00Z&tz=Mars/Olympus").status());
        assertEquals(400, summary("from=2026-03-09T00:00:00Z&to=2026-03-11T00:00:00Z&tz=%2B03:00").status());
        // Prefixed offsets parse as ZoneRegion in Java but Postgres applies the inverted POSIX sign.
        assertEquals(400, summary("from=2026-03-09T00:00:00Z&to=2026-03-11T00:00:00Z&tz=GMT%2B3").status());
        assertEquals(400, summary("from=2026-03-09T00:00:00Z&to=2026-03-11T00:00:00Z&tz=UTC%2B03:00").status());
        assertEquals(400, summary("from=2026-03-09T00:00:00Z&to=2026-03-11T00:00:00Z&tz=UT-5").status());
    }

    @Test
    void acceptsIanaFixedOffsetZones() {
        fixtures.entry(ana, web, at("2026-03-10T22:30:00Z"), at("2026-03-10T23:00:00Z")); // 01:30 on the 11th at UTC+3

        Response response = summary("from=2026-03-10T22:00:00Z&to=2026-03-11T00:00:00Z&tz=Etc/GMT-3");

        assertEquals(new JsonArray().add(new JsonObject().put("date", "2026-03-11").put("seconds", 1800)),
                response.json().getJsonArray("byDay"));
    }
}
