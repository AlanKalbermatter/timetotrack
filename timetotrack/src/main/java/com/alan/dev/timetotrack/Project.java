package com.dev.timetotrack.timetotrack;

public class Project {

  private int projects_id;
  private String projects_name;
  public int customer_id;

  public Project(){
    //default constructor
  }

  public Project(final int projects_id, final String projects_name, final int customer_id) {
    this.projects_id = projects_id;
    this.projects_name = projects_name;
    this.customer_id = customer_id;
  }

    public int getProjects_id() {
        return projects_id;
    }

    public void setProjects_id(int projects_id) {
        this.projects_id = projects_id;
    }

    public String getProjects_name() {
        return projects_name;
    }

    public void setProjects_name(String projects_name) {
        this.projects_name = projects_name;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

}