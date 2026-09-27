package com.timetotrack.timetotrack.http;

import com.timetotrack.timetotrack.error.ValidationException;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JsonFieldsTest {

    @Test
    void optionalStringTrimsAndTreatsBlankAsAbsent() {
        JsonObject body = new JsonObject().put("name", "  Acme  ").put("blank", "   ");

        assertEquals("Acme", JsonFields.optionalString(body, "name"));
        assertNull(JsonFields.optionalString(body, "blank"));
        assertNull(JsonFields.optionalString(body, "missing"));
    }

    @Test
    void rawStringKeepsWhitespace() {
        assertEquals(" pass word ", JsonFields.optionalRawString(new JsonObject().put("password", " pass word "), "password"));
    }

    @Test
    void rejectsNonStringValues() {
        ValidationException error = assertThrows(ValidationException.class,
                () -> JsonFields.optionalString(new JsonObject().put("email", 5), "email"));

        assertEquals("email must be a string", error.getMessage());
    }

    @Test
    void requiredStringReportsMissingField() {
        ValidationException error = assertThrows(ValidationException.class,
                () -> JsonFields.requiredString(new JsonObject(), "name"));

        assertEquals("name is required", error.getMessage());
    }

    @Test
    void requiredIntAcceptsOnlyIntegers() {
        assertEquals(3, JsonFields.requiredInt(new JsonObject().put("customerId", 3), "customerId"));
        assertEquals("customerId must be an integer", assertThrows(ValidationException.class,
                () -> JsonFields.requiredInt(new JsonObject().put("customerId", "3"), "customerId")).getMessage());
        assertEquals("customerId must be an integer", assertThrows(ValidationException.class,
                () -> JsonFields.requiredInt(new JsonObject().put("customerId", 3.5), "customerId")).getMessage());
        assertEquals("customerId is required", assertThrows(ValidationException.class,
                () -> JsonFields.requiredInt(new JsonObject(), "customerId")).getMessage());
    }

    @Test
    void requiredInstantParsesIsoTimestampsWithOffset() {
        assertEquals(Instant.parse("2026-03-10T09:00:00Z"),
                JsonFields.requiredInstant(new JsonObject().put("from", "2026-03-10T09:00:00.000Z"), "from"));
        assertEquals(Instant.parse("2026-03-10T12:00:00Z"),
                JsonFields.requiredInstant(new JsonObject().put("from", "2026-03-10T09:00:00-03:00"), "from"));
        assertThrows(ValidationException.class,
                () -> JsonFields.requiredInstant(new JsonObject().put("from", "2026-03-10 09:00"), "from"));
    }
}
