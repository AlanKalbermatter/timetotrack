package com.timetotrack.timetotrack.config;

/**
 * PostgreSQL connection settings. Defaults come from {@code dbconfig.json}, overridden by {@code DB_*} env vars.
 */
public record DbConfig(String host, int port, String database, String user, String password, int maxPoolSize) {
}
