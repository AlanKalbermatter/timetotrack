package com.timetotrack.timetotrack.http;

import com.timetotrack.timetotrack.error.ValidationException;
import io.vertx.core.json.JsonObject;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

/** Typed reads from request JSON. Every type or presence problem becomes a 400 with the field name. */
public final class JsonFields {

    private JsonFields() {
    }

    public static String optionalString(JsonObject body, String field) {
        String raw = optionalRawString(body, field);
        if (raw == null) {
            return null;
        }
        String trimmed = raw.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    public static String requiredString(JsonObject body, String field) {
        String value = optionalString(body, field);
        if (value == null) {
            throw new ValidationException(field + " is required");
        }
        return value;
    }

    /** Untrimmed read, for values where whitespace is significant (passwords). */
    public static String optionalRawString(JsonObject body, String field) {
        Object value = body.getValue(field);
        if (value == null) {
            return null;
        }
        if (!(value instanceof String text)) {
            throw new ValidationException(field + " must be a string");
        }
        return text;
    }

    public static int requiredInt(JsonObject body, String field) {
        Object value = body.getValue(field);
        if (value == null) {
            throw new ValidationException(field + " is required");
        }
        if (value instanceof Integer number) {
            return number;
        }
        throw new ValidationException(field + " must be an integer");
    }

    public static Instant requiredInstant(JsonObject body, String field) {
        return parseInstant(field, requiredString(body, field));
    }

    public static Instant parseInstant(String field, String raw) {
        try {
            return OffsetDateTime.parse(raw).toInstant();
        } catch (DateTimeParseException e) {
            throw new ValidationException(field + " must be an ISO-8601 timestamp with an offset, e.g. 2026-01-31T09:00:00Z");
        }
    }
}
