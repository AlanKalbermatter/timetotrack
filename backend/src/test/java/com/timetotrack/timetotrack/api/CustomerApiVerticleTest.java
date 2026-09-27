package com.timetotrack.timetotrack.api;

import com.timetotrack.timetotrack.dao.CustomerDao;
import com.timetotrack.timetotrack.service.CustomerService;
import com.timetotrack.timetotrack.support.IntegrationTest;
import com.timetotrack.timetotrack.support.Ports;
import com.timetotrack.timetotrack.support.TestHttp;
import com.timetotrack.timetotrack.support.TestHttp.Response;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.timetotrack.timetotrack.support.Await.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomerApiVerticleTest extends IntegrationTest {

    private static TestHttp http;

    @BeforeAll
    static void deploy() {
        int port = Ports.free();
        await(vertx.deployVerticle(new CustomerApiVerticle(new CustomerService(new CustomerDao(pool)), port)));
        http = new TestHttp(port).asUser(1);
    }

    @Test
    void supportsTheFullLifecycle() {
        Response created = http.post("/api/customers", new JsonObject().put("name", "  Acme Corp "));
        assertEquals(201, created.status());
        int id = created.json().getInteger("id");
        assertEquals("Acme Corp", created.json().getString("name"));

        assertEquals(1, http.get("/api/customers").jsonArray().size());
        assertEquals("Acme Corp", http.get("/api/customers/" + id).json().getString("name"));

        Response renamed = http.put("/api/customers/" + id, new JsonObject().put("name", "Acme Inc"));
        assertEquals(200, renamed.status());
        assertEquals("Acme Inc", renamed.json().getString("name"));

        assertEquals(204, http.delete("/api/customers/" + id).status());
        assertEquals(404, http.get("/api/customers/" + id).status());
    }

    @Test
    void duplicateNamesAreConflicts() {
        fixtures.customer("Acme");

        Response response = http.post("/api/customers", new JsonObject().put("name", "Acme"));

        assertEquals(409, response.status());
        assertEquals("A customer named 'Acme' already exists", response.error());
    }

    @Test
    void blankNamesAreRejected() {
        Response response = http.post("/api/customers", new JsonObject().put("name", "   "));

        assertEquals(400, response.status());
        assertEquals("name is required", response.error());
    }

    @Test
    void invalidIdsAre400AndUnknownIdsAre404() {
        assertEquals(400, http.get("/api/customers/abc").status());
        assertEquals(400, http.delete("/api/customers/0").status());
        assertEquals(404, http.put("/api/customers/999", new JsonObject().put("name", "X")).status());
        assertEquals(404, http.delete("/api/customers/999").status());
    }

    @Test
    void customersWithProjectsCannotBeDeleted() {
        int customerId = fixtures.customer("Acme");
        fixtures.project(customerId, "Web");

        Response response = http.delete("/api/customers/" + customerId);

        assertEquals(409, response.status());
        assertEquals("Customer " + customerId + " still has projects", response.error());
    }
}
