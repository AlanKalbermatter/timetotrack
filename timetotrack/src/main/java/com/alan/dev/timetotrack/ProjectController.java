package com.dev.timetotrack.timetotrack.jdbc;

import com.dev.timetotrack.timetotrack.Project;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLClient;

import java.util.List;

/**
 *
 * @author alan_
 */
public class ProjectController {

    public ProjectController(){
        //constructor
    }


  private SQLClient sql;

  public ProjectController(final Vertx vertx) {
    final JsonObject config = new JsonObject();
    config.put("url", "jdbc:postgresql://localhost/timetotrack");
    config.put("driver_class", "org.postgresql.Driver");
    config.put("max_pool_size", 30);
    config.put("user", "timetotrack");
    config.put("password", "tracktime"); //For production manage password in config file or through env
    sql = JDBCClient.createShared(vertx, config);
  }

  public Future<String> getOne(final String projects_id) {
    final Promise<String> gotOne = Promise.promise();
    final JsonArray params = new JsonArray().add(Integer.parseInt(projects_id));
    sql.updateWithParams("SELECT * FROM projects WHERE projects.projects_id = ?",params, ar -> {
      if (ar.failed()){
        gotOne.fail(ar.cause());
        return;
      }
      if (ar.result().getUpdated() == 0) {
        gotOne.complete();
        return;
      }
      gotOne.complete(projects_id);
    });
    return gotOne.future();
  }
  public Future<String> update(final Project projectUpdate) {
    final Promise<String> updatedProject = Promise.promise();
    final JsonArray params = new JsonArray()
        .add(projectUpdate.getProjects_id())
        .add(projectUpdate.getProjects_name())
        .add(projectUpdate.getCustomer_id());
      sql.updateWithParams("UPDATE projects SET (projects_id, projects_name, customer_id) VALUES (?, ?, ?) WHERE projects.projects_id = ?",
        params, ar -> {
          if (ar.failed()) {
            //Forward error
            updatedProject.fail(ar.cause());
            return;
          }
          //Nothing was updated
          if (ar.result().getUpdated() == 0){
            updatedProject.complete();
            return;
          }
          //Return updated project
          updatedProject.complete();
          });
      //Return response
        return updatedProject.future();
      }

  public Future<JsonArray> getAll() {
    final Promise<JsonArray> getAll = Promise.promise();
    sql.query("SELECT * FROM projects", ar -> {
      if (ar.failed()){
        //Forward error
        getAll.fail(ar.cause());
        return;
      }
      //Return result
      final List<JsonObject> rows = ar.result().getRows();
      final JsonArray result = new JsonArray();
      rows.forEach(result::add);
      getAll.complete(result);
    });
    return getAll.future();
  }

  public Future<Void> addProject(final Project projectToAdd) {
    final Promise<Void> added = Promise.promise();

    final JsonArray params = new JsonArray()
            .add(projectToAdd.getProjects_id())
            .add(projectToAdd.getProjects_name())
            .add(projectToAdd.getCustomer_id());
    sql.updateWithParams("INSERT INTO projects (projects_id, projects_name, customer_id) VALUES (?, ?, ?)", params, ar -> {
      if (ar.failed()){
        //Forward error
        added.fail(ar.cause());
        return;
      }
      //Return failure if updated count is not 1
      if (ar.result().getUpdated() != 1) {
        added.fail(new IllegalStateException("Wrong update count on insert " + ar.result()));
        return;
      }
      //Return success
      added.complete();
    });
    return added.future();
  }

  public Future<String> delete(final String projects_id) {
    final Promise<String> deleted = Promise.promise();
    final JsonArray params = new JsonArray().add(Integer.parseInt(projects_id));
    sql.updateWithParams("DELETE FROM projects WHERE projects.projects_id = ?", params, ar -> {
      if (ar.failed()){
        //Forward error
        deleted.fail(ar.cause());
        return;
      }
      //Nothing was deleted
      if (ar.result().getUpdated() == 0) {
        deleted.complete();
        return;
      }
      //Return deleted projects_id
      deleted.complete(projects_id);
    });
    return deleted.future();
  }
}
