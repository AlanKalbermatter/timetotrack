package com.alan.dev.timetotrack;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

public class MainVerticle extends AbstractVerticle {

  //private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);
  private ProjectStack stack = new ProjectStack();
  @Override
  public void start(Promise<Void> startFuture) throws Exception {
    //LOG.debug("starting...");
    Router projects = Router.router(vertx);
    projects.route().handler(BodyHandler.create());
    projects.route("/*").handler(StaticHandler.create());
    //GET /projects
    getAll(projects);
    //GET /Projects/:isbn -> fetch one project
    getProjectByISBN(projects);
    //POST /project
    createProject(projects);
    //PUT /projects/:isbn
    updateProject(projects);
    //DELETE /projects/:isbn -> delete one project from in memory stack
    deleteProject(projects);

    registerErrorHandler(projects);

    vertx.createHttpServer().requestHandler(projects).listen(8888, http -> {
      if (http.succeeded()) {
        startFuture.complete();
        //LOG.info("HTTP server started on port 8888");
      } else {
        startFuture.fail(http.cause());
      }
    });
  }

  private void deleteProject(final Router projects) {
    projects.delete("/projects/:isbn").handler(req -> {
      final String isbn = req.pathParam("isbn");
      final Project deletedProject = stack.delete(isbn);
      if (null == deletedProject){
        //Project not found
        req.response()
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .setStatusCode(HttpResponseStatus.NOT_FOUND.code())
          .end(new JsonObject().put("error", "Project not found!").encode());
      } else {
        //Deleted a project
        req.response()
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .end(JsonObject.mapFrom(deletedProject).encode());
      }
    });
  }

  private void getProjectByISBN(final Router projects) {
    projects.get("/projects/:isbn").handler(req -> {
      final String isbn = req.pathParam("isbn");
      req.response()
        .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
        .end(JsonObject.mapFrom(stack.get(isbn)).encode());
    });
  }

  private void registerErrorHandler(final Router projects) {
    projects.errorHandler(500, event -> {
      //LOG.error("Failed: ", event.failure());
      if (event.failure() instanceof  IllegalArgumentException) {
        event.response()
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .setStatusCode(HttpResponseStatus.BAD_REQUEST.code())
          .end(new JsonObject().put("error", event.failure().getMessage()).encode());
        return;
      }
      event.response()
        .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
        .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
        .end(new JsonObject().put("error", event.failure().getMessage()).encode());
    });
  }

  private void updateProject(final Router projects) {
    projects.put("/projects/:isbn").handler(req -> {
      final String isbn = req.pathParam("isbn");
      final JsonObject requestBody = req.getBodyAsJson();
      final Project updatedProject = stack.update(isbn, requestBody.mapTo(Project.class));
      //Return response
      req.response()
        .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
        .end(JsonObject.mapFrom(updatedProject).encode());
    });
  }

  private void createProject(final Router projects) {
    projects.post("/projects").handler(req -> {
      // Read body
      final JsonObject requestBody = req.getBodyAsJson();
      System.out.println("Request Body: " + requestBody);
      // Stack
      stack.add(requestBody.mapTo(Project.class));
      // Return response
      req.response()
        .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
        .setStatusCode(HttpResponseStatus.CREATED.code())
        .end(requestBody.encode());
    });
  }

  private void getAll(final Router projects) {
    projects.get("/projects").handler(req -> {
      // Return response
      req.response()
        .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
        .end(stack.getAll().encode());
    });
  }

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new MainVerticle());
  }
}
