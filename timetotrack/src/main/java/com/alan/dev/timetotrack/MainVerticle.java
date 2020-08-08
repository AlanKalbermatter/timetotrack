package com.alan.dev.timetotrack;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.web.Router;

public class MainVerticle extends AbstractVerticle {

	@Override
	public void start() throws Exception {

		final JDBCClient sqlClient = JDBCClient.createShared(vertx,
				new JsonObject().put("url", "jdbc:postgresql://localhost/timetotrack")
						.put("driver_class", "org.postgresql.Driver").put("max_pool_size", 30)
						.put("user", "timetotrack").put("password", "tracktime"));

		DBClient dbClient = new DBClient(sqlClient);

		ProjectController projectController = new ProjectController(dbClient);
		CustomerController customerController = new CustomerController(dbClient);

		Router router = Router.router(super.vertx);

		router.route("/").handler(routingContext -> {
			HttpServerResponse response = routingContext.response();
			response.putHeader("content-type", "text/html").end("<h1>Welcome to Time to Track</h1>");
		});

		router.get("/api/project").handler(projectController::getAll);
		router.post("/api/project").handler(projectController::addOne);
		router.put("/api/project").handler(projectController::updateOne);
		router.delete("/api/project").handler(projectController::deleteProject);

		router.get("/api/customer").handler(customerController::getAll);

		vertx.createHttpServer().requestHandler(router).listen(8082);
	}

}
