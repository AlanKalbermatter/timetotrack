package com.timetotrack.timetotrack.api;

import com.timetotrack.timetotrack.model.User;
import com.timetotrack.timetotrack.service.UserService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.Mockito.*;

@ExtendWith(VertxExtension.class)
public class UserApiVerticleTest {

    private UserService userService;

    @BeforeEach
    void setUp(Vertx vertx, VertxTestContext testContext) {
        userService = mock(UserService.class);

        vertx.deployVerticle(new UserApiVerticle(userService), testContext.succeeding(id -> testContext.completeNow()));
    }

    @Test
    void testCreateUser(Vertx vertx, VertxTestContext testContext) {
        WebClient client = WebClient.create(vertx);

        JsonObject payload = new JsonObject()
                .put("username", "lucas")
                .put("email", "lucas@test.com")
                .put("full_name", "Lucas Ramirez");

        User user = new User(1, "lucas", "lucas@test.com", "Lucas Ramirez");

        doAnswer(invocation -> {
            Handler<AsyncResult<User>> handler = invocation.getArgument(1);
            handler.handle(Future.succeededFuture(user));
            return null;
        }).when(userService).createUser(any(User.class), any());

        client.post(8888, "localhost", "/api/users")
                .sendJsonObject(payload)
                .onComplete(ar -> {
                    if (ar.succeeded()) {
                        assert(ar.result().statusCode() == 201);
                        testContext.completeNow();
                    } else {
                        testContext.failNow(ar.cause());
                    }
                });
    }
}
