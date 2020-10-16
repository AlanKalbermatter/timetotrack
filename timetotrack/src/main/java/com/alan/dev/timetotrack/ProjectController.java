package com.alan.dev.timetotrack;

import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;

public class ProjectController {

    private DBClient dbClient;

    public ProjectController(DBClient dbClient) {
        this.dbClient = dbClient;
    }

  public void getAll(RoutingContext routingContext) {
    dbClient.runQuery(routingContext, "SELECT * FROM projects");
  }

  public void addOne(RoutingContext routingContext) {
        dbClient.add(routingContext,"INSERT INTO projects (projects_id, projects_name, customer_id) VALUES (?, ?, ?)",new JsonArray().add(3));
  }

  public void getOne(RoutingContext routingContext) {
            dbClient.runForOne(routingContext,"SELECT * FROM projects WHERE projects_id = ?", new JsonArray().add(1));
  }

  public void updateOne(RoutingContext routingContext) {
      dbClient.update(routingContext,"UPDATE projects WHERE projects_id = ?", new JsonArray().add(1));
  }

  public void delete(RoutingContext routingContext) {
      dbClient.delete(routingContext,"DELETE FROM projects WHERE projects.projects_id = ?",new JsonArray().add(1));
  }
}
