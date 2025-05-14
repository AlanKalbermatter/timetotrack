package com.timetotrack.timetotrack.api;

import com.timetotrack.timetotrack.model.Customer;
import com.timetotrack.timetotrack.service.CustomerService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomerApiVerticle
        extends AbstractVerticle
        implements VerticleCRUDCommons {

    private final CustomerService customerService;
    private final int port;
    private final Logger LOGGER = LoggerFactory.getLogger(CustomerApiVerticle.class);

    public CustomerApiVerticle(CustomerService customerService, int port) {
        this.customerService = customerService;
        this.port = port;
    }

    @Override
    public void start(Promise<Void> startPromise) {
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        router.post("/api/customers").handler(this::handleCreate);
        router.get("/api/customers").handler(this::handleFetchAll);
        router.get("/api/customers/:id").handler(this::handleFetchById);
        router.put("/api/customers/:id").handler(this::handleUpdate);
        router.delete("/api/customers/:id").handler(this::handleDelete);

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(port, http -> {
                    if (http.succeeded()) {
                        LOGGER.info("Customer API ready at http://localhost:{}/api/customers", port);
                        startPromise.complete();
                    } else {
                        LOGGER.error("Customer API failed to start", http.cause());
                        startPromise.fail(http.cause());
                    }
                });
    }

    @Override
    public void handleCreate(RoutingContext ctx) {
        JsonObject body = ctx.body().asJsonObject();
        Customer customer = body.mapTo(Customer.class);
        customerService.create(customer, ar -> {
            if (ar.succeeded()) {
                LOGGER.info("Created new customer with id {}", customer.getId());
                ctx.response()
                        .setStatusCode(201)
                        .putHeader("Content-Type", "application/json")
                        .end(JsonObject.mapFrom(ar.result()).encode());
            } else {
                LOGGER.error("Failed to create new customer", ar.cause());
                ctx.response().setStatusCode(500).end("Error creating customer");
            }
        });
    }

    @Override
    public void handleFetchAll(RoutingContext ctx) {
        customerService.fetchAll(ar -> {
            if (ar.succeeded()) {
                LOGGER.info("Successfully fetched all customers");
                ctx.response()
                        .putHeader("Content-Type", "application/json")
                        .end(JsonObject.mapFrom(new JsonObject().put("data", ar.result())).encode());
            } else {
                LOGGER.error("Failed to fetch all customers", ar.cause());
                ctx.response().setStatusCode(500).end("Error retrieving customers");
            }
        });
    }

    @Override
    public void handleFetchById(RoutingContext ctx) {
        String idParam = ctx.pathParam("id");
        try {
            int id = Integer.parseInt(idParam);
            customerService.fetchById(id, ar -> {
                if (ar.succeeded()) {
                    LOGGER.info("Successfully retrieved customer with id {}", id);
                    ctx.response()
                            .putHeader("Content-Type", "application/json")
                            .end(JsonObject.mapFrom(ar.result()).encode());
                } else {
                    LOGGER.error("Failed to retrieve customer with id {}", id);
                    ctx.response().setStatusCode(404).end(ar.cause().getMessage());
                }
            });
        } catch (NumberFormatException e) {
            ctx.response().setStatusCode(400).end("Invalid customer ID");
        }
    }

    @Override
    public void handleUpdate(RoutingContext ctx) {
        String idParam = ctx.pathParam("id");
        try {
            int id = Integer.parseInt(idParam);
            JsonObject body = ctx.body().asJsonObject();
            Customer customer = body.mapTo(Customer.class);
            customer.setId(id);
            customerService.update(customer, ar -> {
                if (ar.succeeded()) {
                    LOGGER.info("Updated new customer with id {}", id);
                    ctx.response()
                            .putHeader("Content-Type", "application/json")
                            .end(JsonObject.mapFrom(ar.result()).encode());
                } else {
                    LOGGER.error("Failed to update customer with id {}", id);
                    ctx.response().setStatusCode(500).end("Error updating customer");
                }
            });
        } catch (NumberFormatException e) {
            ctx.response().setStatusCode(400).end("Invalid customer ID");
        }
    }

    @Override
    public void handleDelete(RoutingContext ctx) {
        String idParam = ctx.pathParam("id");
        try {
            int id = Integer.parseInt(idParam);
            customerService.delete(id, ar -> {
                if (ar.succeeded()) {
                    LOGGER.info("Deleted new customer with id {}", id);
                    ctx.response().setStatusCode(204).end();
                } else {
                    LOGGER.error("Failed to delete customer with id {}", id);
                    ctx.response().setStatusCode(500).end("Error deleting customer");
                }
            });
        } catch (NumberFormatException e) {
            ctx.response().setStatusCode(400).end("Invalid customer ID");
        }
    }
}
