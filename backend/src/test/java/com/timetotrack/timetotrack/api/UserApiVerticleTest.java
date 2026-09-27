package com.timetotrack.timetotrack.api;

import com.timetotrack.timetotrack.dao.UserDao;
import com.timetotrack.timetotrack.service.UserService;
import com.timetotrack.timetotrack.support.IntegrationTest;
import com.timetotrack.timetotrack.support.Ports;
import com.timetotrack.timetotrack.support.TestHttp;
import com.timetotrack.timetotrack.support.TestHttp.Response;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.timetotrack.timetotrack.support.Await.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class UserApiVerticleTest extends IntegrationTest {

    private static TestHttp http;

    @BeforeAll
    static void deploy() {
        int port = Ports.free();
        await(vertx.deployVerticle(new UserApiVerticle(new UserService(new UserDao(pool)), port)));
        http = new TestHttp(port);
    }

    @Test
    void listsTeamOrderedByFullNameWithoutSecrets() {
        int zoe = fixtures.user("zoe");
        fixtures.user("ana");

        Response response = http.asUser(zoe).get("/api/users");

        assertEquals(200, response.status());
        List<String> usernames = response.jsonArray().stream()
                .map(user -> ((JsonObject) user).getString("username"))
                .toList();
        assertEquals(List.of("ana", "zoe"), usernames);
        assertFalse(response.body().contains("password"));
    }

    @Test
    void meReturnsTheCallersProfile() {
        int ana = fixtures.user("ana");

        Response response = http.asUser(ana).get("/api/users/me");

        assertEquals(200, response.status());
        assertEquals(new JsonObject()
                .put("id", ana)
                .put("username", "ana")
                .put("email", "ana@example.com")
                .put("fullName", "ana"), response.json());
    }

    @Test
    void meForAnUnknownUserIs404() {
        assertEquals(404, http.asUser(999).get("/api/users/me").status());
    }

    @Test
    void requiresTheGatewayUserHeader() {
        assertEquals(401, http.get("/api/users").status());
    }
}
