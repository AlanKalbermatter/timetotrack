package com.timetotrack.timetotrack.auth;

import io.vertx.ext.auth.HashingStrategy;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * PBKDF2 password hashing via vertx-auth-common, producing self-describing "$pbkdf2$salt$hash" strings.
 * CPU-bound: callers on an event loop must use executeBlocking.
 */
public class PasswordHasher {

    private static final String ALGORITHM = "pbkdf2";
    private static final int SALT_BYTES = 16;

    private final HashingStrategy strategy = HashingStrategy.load();
    private final SecureRandom random = new SecureRandom();

    public String hash(String password) {
        byte[] salt = new byte[SALT_BYTES];
        random.nextBytes(salt);
        return strategy.hash(ALGORITHM, null, Base64.getEncoder().encodeToString(salt), password);
    }

    public boolean verify(String hash, String password) {
        return strategy.verify(hash, password);
    }
}
