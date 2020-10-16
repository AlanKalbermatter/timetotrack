package com.alan.dev.timetotrack;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.web.RoutingContext;

public class CustomerController {

	private DBClient dbClient;

	public CustomerController(DBClient dbClient) {
		this.dbClient = dbClient;
	}

	public void getAll(RoutingContext routingContext) {
		dbClient.runQuery(routingContext, "SELECT * FROM customer");
	}

	public void addOne(RoutingContext routingContext) {
		dbClient.add(routingContext,"INSERT INTO customer (customer_id, customer_name) VALUES (?, ?)",new JsonArray().add(2));
	}

	public void getOne(RoutingContext routingContext) {
		dbClient.runForOne(routingContext,"SELECT * FROM customer WHERE customer_id = ?", new JsonArray().add(1));
	}

	public void updateOne(RoutingContext routingContext) {
		dbClient.update(routingContext,"UPDATE customer WHERE customer_id = ?", new JsonArray().add(1));
	}

	public void delete(RoutingContext routingContext) {
		dbClient.delete(routingContext,"DELETE FROM customer WHERE customer.customer_id = ?",new JsonArray().add(1));
	}

}
