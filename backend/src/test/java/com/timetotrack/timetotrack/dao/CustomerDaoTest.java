package com.timetotrack.timetotrack.dao;

import com.timetotrack.timetotrack.model.Customer;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.sqlclient.Pool;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(VertxExtension.class)
class CustomerDaoTest {

    private static CustomerDao customerDao;

    @BeforeAll
    static void setup(Vertx vertx) {
        Pool client = com.timetotrack.timetotrack.database.DatabaseProvider.createPgPool(vertx);
        customerDao = new CustomerDao(client);
    }

    @Test
    void createAndFetchCustomer(VertxTestContext ctx) {
        Customer newCustomer = new Customer(null, "Test Customer " + System.currentTimeMillis());

        customerDao.create(newCustomer, ar -> {
            assertTrue(ar.succeeded());
            assertNotNull(ar.result().id);

            customerDao.fetchById(ar.result().id, fetched -> {
                assertTrue(fetched.succeeded());
                String name = "Test Customer " + System.currentTimeMillis();
                Customer customer = new Customer(null, name);
                assertEquals(name, fetched.result().name);
                ctx.completeNow();
            });
        });
    }

    @Test
    void fetchAllCustomers(VertxTestContext ctx) {
        customerDao.fetchAll(ar -> {
            assertTrue(ar.succeeded());
            assertNotNull(ar.result());
            ctx.completeNow();
        });
    }
}
