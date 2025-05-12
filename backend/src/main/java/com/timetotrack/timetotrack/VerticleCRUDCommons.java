package com.timetotrack.timetotrack;

import io.vertx.ext.web.RoutingContext;

public interface VerticleCRUDCommons {
    void handleCreate(RoutingContext routingContext);
    void handleFetchAll(RoutingContext routingContext);
    void handleFetchById(RoutingContext routingContext);
    void handleUpdate(RoutingContext routingContext);
    void handleDelete(RoutingContext routingContext);
}
