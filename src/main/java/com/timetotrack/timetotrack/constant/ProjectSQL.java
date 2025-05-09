package com.timetotrack.timetotrack.constant;

public class ProjectSQL {
    public static final String SELECT_ALL = "SELECT project_id, project_name, customer_id FROM projects";
    public static final String SELECT_BY_ID = "SELECT project_id, project_name, customer_id FROM projects WHERE project_id = $1";
    public static final String INSERT_ONE = "INSERT INTO projects (project_name, customer_id) VALUES ($1, $2) RETURNING project_id";
    public static final String UPDATE_ONE = "UPDATE projects SET project_name = $1, customer_id = $2 WHERE project_id = $3";
    public static final String DELETE_BY_ID = "DELETE FROM projects WHERE project_id = $1";
}
