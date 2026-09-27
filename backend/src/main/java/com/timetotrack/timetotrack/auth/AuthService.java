package com.timetotrack.timetotrack.auth;

import com.timetotrack.timetotrack.dao.UserDao;
import com.timetotrack.timetotrack.error.ConflictException;
import com.timetotrack.timetotrack.error.PgErrors;
import com.timetotrack.timetotrack.error.UnauthorizedException;
import com.timetotrack.timetotrack.error.ValidationException;
import com.timetotrack.timetotrack.model.User;
import com.timetotrack.timetotrack.model.UserCredentials;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Registration and login. Emails are normalised to lower case, passwords are hashed with PBKDF2 on the
 * worker pool, and failed logins return the same error whether the email or the password was wrong.
 */
public class AuthService {

    static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_NAME_LENGTH = 100;
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final String INVALID_CREDENTIALS = "Invalid email or password";

    private final Vertx vertx;
    private final UserDao users;
    private final PasswordHasher hasher;
    private final TokenService tokens;

    public AuthService(Vertx vertx, UserDao users, PasswordHasher hasher, TokenService tokens) {
        this.vertx = vertx;
        this.users = users;
        this.hasher = hasher;
        this.tokens = tokens;
    }

    /** All string arguments except the password arrive trimmed, with blank already converted to null. */
    public Future<AuthResult> register(String email, String username, String fullName, String password) {
        String normalizedEmail = normalizeEmail(email);
        if (normalizedEmail == null || !EMAIL.matcher(normalizedEmail).matches()) {
            return Future.failedFuture(new ValidationException("A valid email is required"));
        }
        if (username == null) {
            return Future.failedFuture(new ValidationException("username is required"));
        }
        if (fullName == null) {
            return Future.failedFuture(new ValidationException("fullName is required"));
        }
        if (username.length() > MAX_NAME_LENGTH || fullName.length() > MAX_NAME_LENGTH) {
            return Future.failedFuture(new ValidationException("username and fullName must be at most " + MAX_NAME_LENGTH + " characters"));
        }
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            return Future.failedFuture(new ValidationException("Password must be at least " + MIN_PASSWORD_LENGTH + " characters"));
        }
        return vertx.executeBlocking(() -> hasher.hash(password))
                .compose(hash -> users.create(new User(null, username, normalizedEmail, fullName), hash))
                .recover(PgErrors.translate(PgErrors.UNIQUE_VIOLATION,
                        () -> new ConflictException("Email or username is already registered")))
                .map(user -> new AuthResult(tokens.issue(user), user));
    }

    public Future<AuthResult> login(String email, String password) {
        String normalizedEmail = normalizeEmail(email);
        if (normalizedEmail == null || password == null) {
            return Future.failedFuture(new ValidationException("Email and password are required"));
        }
        return users.findCredentialsByEmail(normalizedEmail).compose(found -> {
            if (found.isEmpty()) {
                return Future.failedFuture(new UnauthorizedException(INVALID_CREDENTIALS));
            }
            UserCredentials credentials = found.get();
            return vertx.executeBlocking(() -> hasher.verify(credentials.passwordHash(), password))
                    .compose(matches -> matches
                            ? Future.succeededFuture(new AuthResult(tokens.issue(credentials.user()), credentials.user()))
                            : Future.failedFuture(new UnauthorizedException(INVALID_CREDENTIALS)));
        });
    }

    private static String normalizeEmail(String email) {
        return email == null ? null : email.toLowerCase(Locale.ROOT);
    }
}
