package com.timetotrack.timetotrack.constant;

public class UserSQL {
    public static final String SELECT_BY_ID =
            "SELECT user_id, username, email, full_name FROM \"user\" WHERE user_id = $1";
    public static final String SELECT_ALL =
            "SELECT user_id, username, email, full_name FROM \"user\"";
    public static final String INSERT_ONE =
            "INSERT INTO \"user\" (username, email, full_name) VALUES ($1, $2, $3) RETURNING user_id";
    public static final String UPDATE_ONE =
            "UPDATE \"user\" SET username = $1, email = $2, full_name = $3 WHERE user_id = $4";
    public static final String DELETE_BY_ID =
            "DELETE FROM \"user\" WHERE user_id = $1";
}
