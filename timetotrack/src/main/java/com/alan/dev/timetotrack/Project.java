package com.alan.dev.timetotrack;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;

public class Project {

    private int projects_id;

    private String projects_name;

    private int customer_id;

    public Project(String projects_name, int customer_id, int projects_id) {
        this.projects_name = projects_name;
        this.customer_id = customer_id;
        this.projects_id = projects_id;
    }

    public Project(JsonObject json) {
        this.projects_name = json.getString("projects_name");
        this.customer_id = json.getInteger("projects_id");
        this.projects_id = json.getInteger("customer_id");
    }

    public Project(int projects_id, String projects_name, int customer_id) {
        this.projects_id = projects_id;
        this.projects_name = projects_name;
        this.customer_id = customer_id;
    }

    public Project(ResultSet result) {

    }

    public Project() {
        
    }

    public JsonArray toJson() {
        JsonObject json = new JsonObject()
                .put("porjects_id",projects_id)
                .put("projects_name",projects_name)
                .put("customer_id",customer_id);
        return null;
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

    public Project setId(int projects_id) {
        this.projects_id = projects_id;
        return this;
    }

    public Project setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
        return this;
    }
}
