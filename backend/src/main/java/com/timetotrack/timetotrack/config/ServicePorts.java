package com.timetotrack.timetotrack.config;

/** Loopback ports of the internal service verticles. Only the gateway port is public. */
public record ServicePorts(int auth, int users, int customers, int projects, int timeEntries, int docs) {

    public static final ServicePorts DEFAULT = new ServicePorts(8893, 8888, 8889, 8890, 8891, 8892);
}
