package com.dev.timetotrack.timetotrack.jdbc;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import com.dev.timetotrack.timetotrack.*;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

public class JDBCMainVerticle extends AbstractVerticle {

  //private static final Logger LOG = LoggerFactory.getLogger(JDBCMainVerticle.class);
  private ProjectController projectController;
  private Cap_userController cap_userController;


  @Override
  public void start() throws Exception {
    //LOG.debug("starting...");
    //Initialize Database Connection
    projectController = new ProjectController(vertx);
    cap_userController = new Cap_userController(vertx);

    //Initialize Web Server Routes
    Router projects = Router.router(vertx);
    projects.route().handler(BodyHandler.create());
    projects.route("/*").handler(StaticHandler.create());

    //GET /projects
    getAll(projects);
    //GET /projects/:projects_id -> fetch one Project
    getOne(projects);
    //POST /projects
    createProject(projects);
    //PUT /projects/:projects_id
    updateProject(projects);
    //DELETE /projects/:projects_id -> delete one Project from in memory stack
    deleteProject(projects);

    /*----------------------------------------------------------------------------------------*/
    registerErrorHandler(projects);
    Router router = Router.router(super.vertx);
    vertx.createHttpServer().requestHandler(router).listen(8082);
        //Falta agregar frontend
        router.route("/").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.putHeader("content-type", "text/html").end("<h1>Welcome to Time to Track</h1>");
        });
  }
  private void getOne (final Router projects){
    projects.get("/projects/:projects_id").handler(req ->{
    final String projects_id = req.pathParam("projects_id");
    projectController.getOne(projects_id).onComplete(ar -> {
        if (ar.failed()) {
          req.fail(ar.cause());
          return;
        }
        if (ar.result() == null) {
          req.response()
            .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
            .setStatusCode(HttpResponseStatus.NOT_FOUND.code())
            .end(new JsonObject().put("error", "Project not found!").encode());
        } else {
          req.response()
            .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
            .end(new JsonObject().put("projects_id", ar.result()).encode());
        }
      });
    });
  }
  public void deleteProject(final Router projects) {
    projects.delete("/projects/:projects_id").handler(req -> {
      final String projects_id = req.pathParam("projects_id");
      projectController.delete(projects_id).onComplete(ar -> {
        if (ar.failed()){
          //Forward error
          req.fail(ar.cause());
          return;
        }
        // Return response
        if (ar.result() == null) {
          //Project not found
          req.response()
            .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
            .setStatusCode(HttpResponseStatus.NOT_FOUND.code())
            .end(new JsonObject().put("error", "Project not found!").encode());
        } else {
          //Deleted a Project
          req.response()
            .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
            .end(new JsonObject().put("projects_id", ar.result()).encode());
        }
      });
    });
  }
  private void updateProject (final Router projects){
    projects.put("/projects/:projects_id").handler(req -> {
      final JsonObject requestBody = req.getBodyAsJson();
      projectController.update(requestBody.mapTo(Project.class)).onComplete(ar -> {
        if (ar.failed()){
          //Forward error
          req.fail(ar.cause());
          return;
        }
        // Return response
        if (ar.result() == null) {
          //Project not found
          req.response()
            .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
            .setStatusCode(HttpResponseStatus.NOT_FOUND.code())
            .end(new JsonObject().put("error", "Project not found!").encode());
        } else {
          //Updated Project
          req.response()
            .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
            .end(new JsonObject().put("projects_id", ar.result()).encode());
        }
      });
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

  private void createProject(final Router projects) {
    projects.post("/projects").handler(req -> {
      // Read body
      final JsonObject requestBody = req.getBodyAsJson();
      System.out.println("Request Body: " + requestBody);
      // stack
      projectController.addProject(requestBody.mapTo(Project.class)).onComplete(ar -> {
        if (ar.failed()){
          //Forward error
          req.fail(ar.cause());
          return;
        }
        // Return response
        req.response()
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .setStatusCode(HttpResponseStatus.CREATED.code())
          .end(requestBody.encode());
      });
    });
  }

  private void getAll(final Router projects) {
    projects.get("/projects").handler(req -> {
      projectController.getAll().onComplete(ar -> {
        if (ar.failed()){
          //Forward error
          req.fail(ar.cause());
          return;
        }
        // Return response
        req.response()
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .end(ar.result().encode());
      });
    });
  }

  /*--------------------------------------------------------------------------------------------*/
  /*private void createCap_user(final Router cap_user) {
    cap_user.post("/cap_user").handler(req -> {
      // Read body
      final JsonObject requestBody = req.getBodyAsJson();
      System.out.println("Request Body: " + requestBody);
      // stack
      cap_userController.addCap_user(requestBody.mapTo(Cap_user.class)).onComplete(ar -> {
        if (ar.failed()){
          //Forward error
          req.fail(ar.cause());
          return;
        }
        // Return response
        req.response()
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .setStatusCode(HttpResponseStatus.CREATED.code())
          .end(requestBody.encode());
      });
    });
  }
  */
  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new JDBCMainVerticle());
  }
}
