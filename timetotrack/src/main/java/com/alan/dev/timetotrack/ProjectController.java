package com.alan.dev.timetotrack;

import io.vertx.ext.web.RoutingContext;

public class ProjectController {

	private DBClient dbClient;

	public ProjectController(DBClient dbClient) {
		this.dbClient = dbClient;
	}

	public void addOne(RoutingContext routingContext) {
        /* extraer JSONObject del body del request
         * pasarselo a DBClient con un insert + los valores para ese insert
         * DBClient o este deberían escribir la respuesta como un Json con los datos del project
         */
		
		
		/*
		 final String isbn = req.pathParam("isbn");
      final JsonObject requestBody = req.getBodyAsJson();
      final Book updatedBook = store.update(isbn, requestBody.mapTo(Book.class));
      //Return response
      req.response()
        .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
        .end(JsonObject.mapFrom(updatedBook).encode()); 
		 
		 */
		dbClient.executeDml("");
	}

	public void getOne(RoutingContext routingContext) {
		/* similar a getAll peeeero un solo JSONOBject 
		 hay que crear algo en DBClient algo que traiga un solo y que además no tenga corchetes [] */
		

	}

	public void updateOne(RoutingContext routingContext) {

	}

	public void deleteProject(RoutingContext routingContext) {

	}

	public void getAll(RoutingContext routingContext) {
		dbClient.runQuery(routingContext, "SELECT * FROM projects");
	}

}
