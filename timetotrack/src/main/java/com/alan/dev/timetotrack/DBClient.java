package com.alan.dev.timetotrack;

import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.web.RoutingContext;

public class DBClient {

	private JDBCClient jdbcClient;

	public DBClient(JDBCClient jdbcClient) {
		this.jdbcClient = jdbcClient;
	}

	public void runQuery(RoutingContext routingContext, String sql) {
		jdbcClient.getConnection(ar -> {
			SQLConnection connection = ar.result();
			connection.query(sql, result -> {

				routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
						.end(result.result().getResults().toString());
				connection.close();
			});
		});
	}
	
	public void executeDml(String sql) {
//		jdbcClient.updateWithParams(sql, params, handler);

	}
}
