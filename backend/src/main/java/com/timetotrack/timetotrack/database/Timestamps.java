package com.timetotrack.timetotrack.database;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/** The reactive PG client binds timestamptz as OffsetDateTime; the domain uses Instant. */
public final class Timestamps {

    private Timestamps() {
    }

    public static OffsetDateTime toDb(Instant instant) {
        return instant == null ? null : instant.atOffset(ZoneOffset.UTC);
    }

    public static Instant fromDb(OffsetDateTime value) {
        return value == null ? null : value.toInstant();
    }
}
