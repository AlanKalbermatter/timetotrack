package com.timetotrack.timetotrack.model;

public class Project {
    private Integer id;
    private String name;
    private Integer customerId;

    public Project() {}

    public Project(Integer id, String name, Integer customerId) {
        this.id = id;
        this.name = name;
        this.customerId = customerId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }
}
