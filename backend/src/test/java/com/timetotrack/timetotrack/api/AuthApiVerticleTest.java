package com.timetotrack.timetotrack.api;

import com.timetotrack.timetotrack.auth.AuthService;
import com.timetotrack.timetotrack.auth.PasswordHasher;
import com.timetotrack.timetotrack.auth.TokenService;
import com.timetotrack.timetotrack.dao.UserDao;
import com.timetotrack.timetotrack.support.IntegrationTest;
import com.timetotrack.timetotrack.support.Ports;
import com.timetotrack.timetotrack.support.TestHttp;
import com.timetotrack.timetotrack.support.TestHttp.Response;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.timetotrack.timetotrack.support.Await.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthApiVerticleTest extends IntegrationTest {

    private static TestHttp http;
    private static TokenService tokens;

    @BeforeAll
    static void deploy() {
        tokens = new TokenService(vertx, "test-secret");
        AuthService auth = new AuthService(vertx, new UserDao(pool), new PasswordHasher(), tokens);
        int port = Ports.free();
        await(vertx.deployVerticle(new AuthApiVerticle(auth, port)));
        http = new TestHttp(port);
    }

    private static JsonObject registration(String email, String username) {
        return new JsonObject()
                .put("email", email)
                .put("username", username)
                .put("fullName", "Ana Gómez")
                .put("password", "correct-horse");
    }

    @Test
    void registerReturnsATokenForTheNewUser() {
        Response response = http.post("/api/auth/register", registration("  Ana@Example.com ", "ana"));

        assertEquals(201, response.status());
        JsonObject user = response.json().getJsonObject("user");
        assertEquals("ana@example.com", user.getString("email"));
        assertEquals("Ana Gómez", user.getString("fullName"));
        assertEquals(user.getInteger("id"), await(tokens.verify(response.json().getString("token"))));
        assertFalse(response.body().contains("password"));
    }

    @Test
    void registerRejectsADuplicateEmailIgnoringCase() {
        assertEquals(201, http.post("/api/auth/register", registration("ana@example.com", "ana")).status());

        Response duplicate = http.post("/api/auth/register", registration("ANA@example.com", "ana2"));

        assertEquals(409, duplicate.status());
        assertEquals("Email or username is already registered", duplicate.error());
    }

    @Test
    void registerValidatesInput() {
        assertEquals("A valid email is required",
                http.post("/api/auth/register", registration("not-an-email", "ana")).error());
        assertEquals("username is required",
                http.post("/api/auth/register", registration("ana@example.com", "  ")).error());
        assertEquals("Password must be at least 8 characters",
                http.post("/api/auth/register", registration("ana@example.com", "ana").put("password", "short")).error());
        Response numberEmail = http.post("/api/auth/register", registration("x", "ana").put("email", 5));
        assertEquals(400, numberEmail.status());
        assertEquals("email must be a string", numberEmail.error());
    }

    @Test
    void loginAcceptsEmailCaseAndWhitespaceVariants() {
        http.post("/api/auth/register", registration("ana@example.com", "ana"));

        Response response = http.post("/api/auth/login",
                new JsonObject().put("email", " Ana@Example.COM ").put("password", "correct-horse"));

        assertEquals(200, response.status());
        assertTrue(response.json().getString("token").length() > 20);
        assertEquals("ana", response.json().getJsonObject("user").getString("username"));
    }

    @Test
    void loginFailsTheSameWayForWrongPasswordAndUnknownEmail() {
        http.post("/api/auth/register", registration("ana@example.com", "ana"));

        Response wrongPassword = http.post("/api/auth/login",
                new JsonObject().put("email", "ana@example.com").put("password", "incorrect"));
        Response unknownEmail = http.post("/api/auth/login",
                new JsonObject().put("email", "bob@example.com").put("password", "correct-horse"));

        assertEquals(401, wrongPassword.status());
        assertEquals(401, unknownEmail.status());
        assertEquals(wrongPassword.error(), unknownEmail.error());
    }
}
