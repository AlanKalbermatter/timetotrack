package com.alan.dev.timetotrack;


import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.web.RoutingContext;
import io.vertx.rxjava.core.Future;

import java.util.List;
import java.util.stream.Collectors;

import static org.mvel2.DataTypes.COLLECTION;

public class ProjectControl {


  public void addOne(RoutingContext routingContext) {
    final Project project = Json.decodeValue(routingContext.getBodyAsString(),
      Project.class);
    // Here handle the creation of new projects
    router.post("/projects").handler(ctx -> {
      HttpServerResponse response = ctx.response();
      SQLConnection conn = ctx.get("conn");

      // We need to instruct PostgreSQL that the String (again no conversion needed) is a JSON document
      conn.updateWithParams("INSERT INTO projects (project) VALUES (?::JSON)",
        new JsonArray().add(ctx.getBodyAsString()), query-> {
          if (query.failed()) {
            ctx.fail(query.cause());
            return;
          }

          response.setStatusCode(201).end();
        });
    });
  }

  public void getOne(RoutingContext routingContext) {
    final String id = routingContext.request().getParam("id");
    if (id == null) {
      routingContext.response().setStatusCode(400).end();
    } else {
      mongo.findOne(COLLECTION, new JsonObject().put("_id", id), null, ar -> {
        if (ar.succeeded()) {
          if (ar.result() == null) {
            routingContext.response().setStatusCode(404).end();
            return;
          }
          Whisky whisky = new Whisky(ar.result());
          routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "application/json; charset=utf-8")
            .end(Json.encodePrettily(whisky));
        } else {
          routingContext.response().setStatusCode(404).end();
        }
      });
    }
  }


  public void updateOne(RoutingContext routingContext) {
    final String projects_id = routingContext.request().getParam("projects_id");
    JsonObject json = routingContext.getBodyAsJson();
    if (id == null || json == null) {
      routingContext.response().setStatusCode(400).end();
    } else {
      mongo.update(COLLECTION,
        new JsonObject().put("_projects_id", projects_id), // Select a unique document
        // The update syntax: {$set, the json object containing the fields to update}
        new JsonObject()
          .put("$set", json),
        v -> {
          if (v.failed()) {
            routingContext.response().setStatusCode(404).end();
          } else {
            routingContext.response()
              .putHeader("content-type", "application/json; charset=utf-8")
              .end(Json.encodePrettily(new Project(projects_id, json.getString("project_name"))));
          }
        });
    }
  }

  public void deleteProject(RoutingContext routingContext){
    String id = routingContext.request().getParam("id");
    if (id == null) {
      routingContext.response().setStatusCode(400).end();
    } else {
      mongo.removeOne(COLLECTION, new JsonObject().put("_id", id),
        ar -> routingContext.response().setStatusCode(204).end());
    }
  }
  public void getAll(RoutingContext routingContext) {
    mongo.find(COLLECTION, new JsonObject(), results -> {
      List<JsonObject> objects = results.result();
      List<Project> whiskies = objects.stream().map(Project::new).collect(Collectors.toList());
      routingContext.response()
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(Json.encodePrettily(whiskies));
    });
  }
  private void createSomeData(Handler<AsyncResult<Void>> next, Future<Void> fut) {

  }
  }
