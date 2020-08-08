package com.alan.dev.timetotrack;

import io.vertx.core.json.JsonObject;

public class Project {

  private int projects_id;

  private String projects_name;

  private int customer_id;

  String id = String.valueOf(Integer.parseInt(""));

  public Project(String projects_name, int customer_id) {
    this.projects_name = projects_name;
    this.customer_id = customer_id;
    this.projects_id = Integer.parseInt("");
  }

  public Project(JsonObject json) {
    this.projects_name = json.getString("projects_name");
    this.customer_id = json.getInteger("projects_id");
    this.projects_id = json.getInteger("customer_id");
  }

  public Project() {
    this.projects_id = Integer.parseInt("");
  }

  public Project(int projects_id, String projects_name, int customer_id){
    this.projects_id = projects_id;
    this.projects_name = projects_name;
    this.customer_id = customer_id;
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject()
      .put("name", projects_name)
      .put("customer id",customer_id);
    if (id != null && !id.isEmpty()) {
      json.put("_project_id", projects_id);
    }
    return json;
  }

  public String getProjects_name() {
    return projects_name;
  }

  public int getProjects_id() {
    return projects_id;
  }

  public int getCustomer_id() {
    return customer_id;
  }

  public Project setProject_name(String projects_name) {
    this.projects_name = projects_name;
    return this;
  }

  public Project setProject_id(int projects_id) {
    this.projects_id = projects_id;
    return this;
  }

  public Project setCustomer_id(int customer_id) {
    this.customer_id = customer_id;
    return this;
  }
}
