package com.timetotrack.timetotrack.api;

import com.timetotrack.timetotrack.model.Customer;
import com.timetotrack.timetotrack.service.CustomerService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class CustomerApiVerticle extends AbstractVerticle {

    private final CustomerService customerService;
    private final int port;

    public CustomerApiVerticle(CustomerService customerService, int port) {
        this.customerService = customerService;
        this.port = port;
    }

    @Override
    public void start(Promise<Void> startPromise) {
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        router.post("/api/customers").handler(this::handleCreateCustomer);
        router.get("/api/customers").handler(this::handleGetAll);
        router.get("/api/customers/:id").handler(this::handleGetById);
        router.put("/api/customers/:id").handler(this::handleUpdateCustomer);
        router.delete("/api/customers/:id").handler(this::handleDeleteCustomer);

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(port, http -> {
                    if (http.succeeded()) {
                        System.out.println("Customer API ready at http://localhost:" + port + "/api/customers");
                        startPromise.complete();
                    } else {
                        startPromise.fail(http.cause());
                    }
                });
    }

    private void handleCreateCustomer(RoutingContext ctx) {
        JsonObject body = ctx.body().asJsonObject();
        Customer customer = body.mapTo(Customer.class);
        customerService.create(customer, ar -> {
            if (ar.succeeded()) {
                ctx.response()
                        .setStatusCode(201)
                        .putHeader("Content-Type", "application/json")
                        .end(JsonObject.mapFrom(ar.result()).encode());
            } else {
                ctx.response().setStatusCode(500).end("Error creating customer");
            }
        });
    }

    private void handleGetAll(RoutingContext ctx) {
        customerService.fetchAll(ar -> {
            if (ar.succeeded()) {
                ctx.response()
                        .putHeader("Content-Type", "application/json")
                        .end(JsonObject.mapFrom(new JsonObject().put("data", ar.result())).encode());
            } else {
                ctx.response().setStatusCode(500).end("Error retrieving customers");
            }
        });
    }

    private void handleGetById(RoutingContext ctx) {
        String idParam = ctx.pathParam("id");
        try {
            int id = Integer.parseInt(idParam);
            customerService.fetchById(id, ar -> {
                if (ar.succeeded()) {
                    ctx.response()
                            .putHeader("Content-Type", "application/json")
                            .end(JsonObject.mapFrom(ar.result()).encode());
                } else {
                    ctx.response().setStatusCode(404).end(ar.cause().getMessage());
                }
            });
        } catch (NumberFormatException e) {
            ctx.response().setStatusCode(400).end("Invalid customer ID");
        }
    }

    private void handleUpdateCustomer(RoutingContext ctx) {
        String idParam = ctx.pathParam("id");
        try {
            int id = Integer.parseInt(idParam);
            JsonObject body = ctx.body().asJsonObject();
            Customer customer = body.mapTo(Customer.class);
            customer.setId(id);
            customerService.update(customer, ar -> {
                if (ar.succeeded()) {
                    ctx.response()
                            .putHeader("Content-Type", "application/json")
                            .end(JsonObject.mapFrom(ar.result()).encode());
                } else {
                    ctx.response().setStatusCode(500).end("Error updating customer");
                }
            });
        } catch (NumberFormatException e) {
            ctx.response().setStatusCode(400).end("Invalid customer ID");
        }
    }

    private void handleDeleteCustomer(RoutingContext ctx) {
        String idParam = ctx.pathParam("id");
        try {
            int id = Integer.parseInt(idParam);
            customerService.delete(id, ar -> {
                if (ar.succeeded()) {
                    ctx.response().setStatusCode(204).end();
                } else {
                    ctx.response().setStatusCode(500).end("Error deleting customer");
                }
            });
        } catch (NumberFormatException e) {
            ctx.response().setStatusCode(400).end("Invalid customer ID");
        }
    }
}
