package com.timetotrack.timetotrack.constant;

public final class ProjectSQL {

    private static final String SELECT =
            "SELECT p.project_id, p.project_name, p.customer_id, c.customer_name "
                    + "FROM projects p JOIN customer c ON c.customer_id = p.customer_id ";

    public static final String SELECT_ALL = SELECT + "ORDER BY c.customer_name, p.project_name";
    public static final String SELECT_BY_ID = SELECT + "WHERE p.project_id = $1";
    public static final String INSERT_ONE =
            "INSERT INTO projects (project_name, customer_id) VALUES ($1, $2) RETURNING project_id";
    public static final String UPDATE_ONE =
            "UPDATE projects SET project_name = $1, customer_id = $2 WHERE project_id = $3";
    public static final String DELETE_BY_ID = "DELETE FROM projects WHERE project_id = $1";

    private ProjectSQL() {
    }
}
