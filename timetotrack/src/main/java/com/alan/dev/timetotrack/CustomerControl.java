package com.alan.dev.timetotrack;


import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

public class CustomerControl {

  public void addOne(RoutingContext routingContext) {
    final Customer customer = Json.decodeValue(routingContext.getBodyAsString(),
      Customer.class);


  }

  public void getOne(RoutingContext routingContext) {

  }

  public void updateOne(RoutingContext routingContext) {

  }

  public void deleteCustomer(RoutingContext routingContext) {

  }

  public void getAll(RoutingContext routingContext) {

  }
  //createSomeData!!!

}
