package com.timetotrack.timetotrack.constant;

public final class TimeEntrySQL {

    private static final String SELECT =
            "SELECT te.time_entry_id, te.user_id, te.project_id, p.project_name, te.description, te.from_time, te.to_time "
                    + "FROM time_entry te JOIN projects p ON p.project_id = te.project_id ";

    /** $1 user, $2 range start, $3 range end, $4 now (end of running entries). Overlap semantics. */
    public static final String SELECT_FOR_USER_IN_RANGE = SELECT
            + "WHERE te.user_id = $1 AND te.from_time < $3 AND COALESCE(te.to_time, $4) > $2 "
            + "ORDER BY te.from_time DESC";
    public static final String SELECT_BY_ID_FOR_USER = SELECT + "WHERE te.time_entry_id = $1 AND te.user_id = $2";
    public static final String SELECT_RUNNING_FOR_USER = SELECT + "WHERE te.user_id = $1 AND te.to_time IS NULL";
    public static final String INSERT_ONE =
            "INSERT INTO time_entry (user_id, project_id, description, from_time, to_time) "
                    + "VALUES ($1, $2, $3, $4, $5) RETURNING time_entry_id";
    /** Ends the running entry at $2, nudged 1 ms past from_time so the CHECK constraint always holds. */
    public static final String STOP_RUNNING_FOR_USER =
            "UPDATE time_entry SET to_time = GREATEST($2, from_time + interval '1 millisecond') "
                    + "WHERE user_id = $1 AND to_time IS NULL RETURNING time_entry_id";
    public static final String DELETE_FOR_USER = "DELETE FROM time_entry WHERE time_entry_id = $1 AND user_id = $2";

    /** Seconds of the entry inside [$2, $3), running entries ending at $4. Shared by both summary queries. */
    private static final String CLIPPED_SECONDS =
            "CAST(SUM(EXTRACT(EPOCH FROM LEAST(COALESCE(te.to_time, $4), $3) - GREATEST(te.from_time, $2))) AS BIGINT)";
    private static final String OVERLAPS_RANGE =
            "te.user_id = $1 AND te.from_time < $3 AND COALESCE(te.to_time, $4) > $2";

    /** $1 user, $2 start, $3 end, $4 now. */
    public static final String SUMMARY_BY_PROJECT =
            "SELECT p.project_id, p.project_name, " + CLIPPED_SECONDS + " AS seconds "
                    + "FROM time_entry te JOIN projects p ON p.project_id = te.project_id "
                    + "WHERE " + OVERLAPS_RANGE + " "
                    + "GROUP BY p.project_id, p.project_name ORDER BY seconds DESC, p.project_name";

    /** $1 user, $2 start, $3 end, $4 now, $5 IANA time zone. */
    public static final String SUMMARY_BY_DAY =
            "SELECT to_char(GREATEST(te.from_time, $2) AT TIME ZONE $5, 'YYYY-MM-DD') AS day, " + CLIPPED_SECONDS + " AS seconds "
                    + "FROM time_entry te "
                    + "WHERE " + OVERLAPS_RANGE + " "
                    + "GROUP BY day ORDER BY day";

    private TimeEntrySQL() {
    }
}
