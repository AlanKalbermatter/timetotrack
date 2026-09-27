package com.timetotrack.timetotrack.api;

import com.timetotrack.timetotrack.http.Http;
import com.timetotrack.timetotrack.http.JsonFields;
import com.timetotrack.timetotrack.http.ServiceVerticle;
import com.timetotrack.timetotrack.model.Customer;
import com.timetotrack.timetotrack.service.CustomerService;
import io.vertx.ext.web.Router;

public class CustomerApiVerticle extends ServiceVerticle {

    private final CustomerService customers;

    public CustomerApiVerticle(CustomerService customers, int port) {
        super(port);
        this.customers = customers;
    }

    @Override
    protected void routes(Router router) {
        router.get("/api/customers").handler(ctx -> Http.respond(ctx, 200,
                () -> customers.findAll().map(list -> Http.toArray(list, Customer::toJson))));
        router.post("/api/customers").handler(ctx -> Http.respond(ctx, 201,
                () -> customers.create(JsonFields.optionalString(Http.body(ctx), "name")).map(Customer::toJson)));
        router.get("/api/customers/:id").handler(ctx -> Http.respond(ctx, 200,
                () -> customers.findById(Http.pathIntId(ctx, "id")).map(Customer::toJson)));
        router.put("/api/customers/:id").handler(ctx -> Http.respond(ctx, 200,
                () -> customers.update(Http.pathIntId(ctx, "id"), JsonFields.optionalString(Http.body(ctx), "name"))
                        .map(Customer::toJson)));
        router.delete("/api/customers/:id").handler(ctx -> Http.respond(ctx, 204,
                () -> customers.delete(Http.pathIntId(ctx, "id"))));
    }
}
