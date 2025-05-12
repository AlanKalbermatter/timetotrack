package com.timetotrack.timetotrack.constant;

public class TimeEntrySQL {
    public static final String INSERT_ONE = "INSERT INTO time_entry (from_time, to_time, project_id, user_id) VALUES (?, ?, ?, ?) RETURNING time_entry_id";
    public static final String SELECT_ALL = "SELECT * FROM time_entry";
    public static final String SELECT_BY_ID = "SELECT * FROM time_entry WHERE time_entry_id = ?";
    public static final String UPDATE_ONE = "UPDATE time_entry SET from_time = ?, to_time = ?, project_id = ?, user_id = ? WHERE time_entry_id = ?";
    public static final String DELETE_BY_ID = "DELETE FROM time_entry WHERE time_entry_id = ?";
    public static final String SELECT_BY_USER_ID = "SELECT * FROM time_entry WHERE user_id = ?";
}
