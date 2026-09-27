package com.timetotrack.timetotrack.constant;

public final class UserSQL {

    private static final String COLUMNS = "user_id, username, email, full_name";

    public static final String SELECT_ALL = "SELECT " + COLUMNS + " FROM \"user\" ORDER BY full_name, username";
    public static final String SELECT_BY_ID = "SELECT " + COLUMNS + " FROM \"user\" WHERE user_id = $1";
    public static final String SELECT_CREDENTIALS_BY_EMAIL =
            "SELECT " + COLUMNS + ", password_hash FROM \"user\" WHERE email = $1";
    public static final String INSERT_ONE =
            "INSERT INTO \"user\" (username, email, full_name, password_hash) VALUES ($1, $2, $3, $4) RETURNING user_id";

    private UserSQL() {
    }
}
