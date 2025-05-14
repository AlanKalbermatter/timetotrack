package com.timetotrack.timetotrack.api;

import com.timetotrack.timetotrack.database.DatabaseProvider;
import com.timetotrack.timetotrack.dependencyInjection.AppComponent;
import com.timetotrack.timetotrack.dependencyInjection.AppModule;
import com.timetotrack.timetotrack.dependencyInjection.DaggerAppComponent;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(VertxExtension.class)
public class CustomerApiVerticleTest {

    private static final AtomicInteger dynamicPort = new AtomicInteger(8889);
    private static WebClient client;

    @BeforeAll
    static void setup(Vertx vertx, VertxTestContext ctx) {
        int port = dynamicPort.get();

        AppComponent component = DaggerAppComponent.builder()
                .appModule(new AppModule(vertx, DatabaseProvider.createPgPool(vertx), 8889))
                .build();

        vertx.deployVerticle(component.customerApiVerticle(), ar -> {
            if (ar.succeeded()) {
                client = WebClient.create(vertx);
                ctx.completeNow();
            } else {
                ctx.failNow(ar.cause());
            }
        });
    }

    @Test
    void testCreateCustomer(VertxTestContext ctx) {
        JsonObject json = new JsonObject().put("name", "Customer Test " + System.currentTimeMillis());

        client.post(dynamicPort.get(), "localhost", "/api/customers")
                .sendJsonObject(json, ar -> {
                    if (ar.failed()) {
                        ctx.failNow(ar.cause());
                        return;
                    }
                    assertEquals(201, ar.result().statusCode());
                    assertNotNull(ar.result().bodyAsJsonObject().getInteger("id"));
                    ctx.completeNow();
                });
    }

    @Test
    void testGetAllCustomers(VertxTestContext ctx) {
        client.get(dynamicPort.get(), "localhost", "/api/customers")
                .send(ar -> {
                    if (ar.failed()) {
                        ctx.failNow(ar.cause());
                        return;
                    }
                    assertEquals(200, ar.result().statusCode());
                    assertTrue(ar.result().bodyAsJsonArray().size() >= 0);
                    ctx.completeNow();
                });
    }
}
