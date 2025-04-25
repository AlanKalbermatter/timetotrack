package com.timetotrack.timetotrack.api;

import com.timetotrack.timetotrack.model.User;
import com.timetotrack.timetotrack.service.UserService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(VertxExtension.class)
public class UserApiVerticleTest {

    private UserService userService;

    @BeforeEach
    void setup(Vertx vertx, VertxTestContext testContext) {
        userService = mock(UserService.class);

        List<User> mockUsers = List.of(
                new User(1, "testuser", "test@mail.com", "Test User")
        );

        doAnswer(inv -> {
            inv.<io.vertx.core.Handler<io.vertx.core.AsyncResult<List<User>>>>getArgument(0)
                    .handle(io.vertx.core.Future.succeededFuture(mockUsers));
            return null;
        }).when(userService).getAllUsers(any());

        vertx.deployVerticle(new UserApiVerticle(userService), testContext.succeeding(id -> testContext.completeNow()));
    }

    @Test
    void testGetUsersEndpoint(Vertx vertx, VertxTestContext testContext) {
        WebClient client = WebClient.create(vertx);
        client.get(8888, "localhost", "/api/users")
                .send()
                .onComplete(ar -> {
                    if (ar.succeeded()) {
                        assertEquals(200, ar.result().statusCode());
                        JsonArray body = ar.result().bodyAsJsonArray();
                        assertEquals("testuser", body.getJsonObject(0).getString("username"));
                        testContext.completeNow();
                    } else {
                        testContext.failNow(ar.cause());
                    }
                });
    }

    @Test
    void testCreateUserEndpoint(Vertx vertx, VertxTestContext testContext) {
        WebClient client = WebClient.create(vertx);

        JsonObject payload = new JsonObject()
                .put("username", "lucas")
                .put("email", "lucas@test.com")
                .put("full_name", "Lucas Ramirez");

        User createdUser = new User(100, "lucas", "lucas@test.com", "Lucas Ramirez");

        doAnswer(invocation -> {
            Handler<AsyncResult<User>> handler = invocation.getArgument(1);
            handler.handle(Future.succeededFuture(createdUser));
            return null;
        }).when(userService).createUser(any(User.class), any());

        client.post(8888, "localhost", "/api/users")
                .sendJsonObject(payload)
                .onComplete(ar -> {
                    if (ar.succeeded()) {
                        assertEquals(201, ar.result().statusCode());
                        JsonObject body = ar.result().bodyAsJsonObject();
                        assertEquals("lucas", body.getString("username"));
                        assertEquals(100, body.getInteger("id"));
                        testContext.completeNow();
                    } else {
                        testContext.failNow(ar.cause());
                    }
                });
    }

}
