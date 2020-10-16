package com.alan.dev.timetotrack;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

public class MainVerticle extends AbstractVerticle {

	@Override
	public void start() throws Exception {

		final JDBCClient sqlClient = JDBCClient.createShared(vertx,
				new JsonObject()
						.put("url", "jdbc:postgresql://localhost/timetotrack")
						.put("driver_class", "org.postgresql.Driver")
						.put("max_pool_size", 30)
						.put("user", "timetotrack")
						.put("password", "tracktime"));

		DBClient dbClient = new DBClient(sqlClient);
		ProjectController projectController = new ProjectController(dbClient);
		CustomerController customerController = new CustomerController(dbClient);
		Router router = Router.router(vertx);
		//router.route("*/").handler(StaticHandler.create());

		router.route("/").handler(routingContext -> {
			HttpServerResponse response = routingContext.response();
			response.putHeader("content-type", "text/html").end("<h1>Welcome to TimeToTrack</h1>");
		});
		router.get("/api/projects").handler(projectController::getAll);
		router.post("/api/projects").handler(projectController::addOne);
		router.get("/api/projects/:projects_id").handler(projectController::getOne);
		router.put("/api/projects/:projects_id").handler(projectController::updateOne);
		router.delete("/api/projects").handler(projectController::delete);
		//--Customer methods calls.
		router.get("/api/customer").handler(customerController::getAll);
		router.post("api/customer").handler(customerController::addOne);
		router.get("/api/customer/:customer_id").handler(customerController::getOne);
		router.put("/api/customer/:customer_id").handler(customerController::updateOne);
		router.delete("/api/customer").handler(customerController::delete);
		//--CapUser methods calls.
		router.get("/api/customer").handler(capUserController::getAll);

		vertx.createHttpServer().requestHandler(router).listen(8082);
	}

	}
