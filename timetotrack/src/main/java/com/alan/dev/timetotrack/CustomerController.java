package com.alan.dev.timetotrack;

import io.vertx.core.json.Json;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.web.RoutingContext;

public class CustomerController {

	private DBClient dbClient;

	public CustomerController(DBClient dbClient) {
		this.dbClient = dbClient;
	}

	public void addOne(RoutingContext routingContext) {
		final Customer customer = Json.decodeValue(routingContext.getBodyAsString(), Customer.class);

	}

	public void getOne(RoutingContext routingContext) {

	}

	public void updateOne(RoutingContext routingContext) {

	}

	public void deleteCustomer(RoutingContext routingContext) {

	}

	public void getAll(RoutingContext routingContext) {
		dbClient.runQuery(routingContext, "SELECT * FROM customer");
	}
	// createSomeData!!!

}
