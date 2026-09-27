package com.timetotrack.timetotrack.auth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordHasherTest {

    private final PasswordHasher hasher = new PasswordHasher();

    @Test
    void verifiesTheOriginalPasswordOnly() {
        String hash = hasher.hash("correct horse");

        assertTrue(hasher.verify(hash, "correct horse"));
        assertFalse(hasher.verify(hash, "correct horse "));
        assertFalse(hasher.verify(hash, "wrong"));
    }

    @Test
    void saltsEveryHash() {
        String first = hasher.hash("same-password");
        String second = hasher.hash("same-password");

        assertNotEquals(first, second);
        assertTrue(first.startsWith("$pbkdf2$"));
        assertFalse(first.contains("same-password"));
    }
}
