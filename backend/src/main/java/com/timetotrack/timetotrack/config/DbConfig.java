package com.timetotrack.timetotrack.config;

public record DbConfig(String host, int port, String database, String user, String password, int maxPoolSize) {
}
