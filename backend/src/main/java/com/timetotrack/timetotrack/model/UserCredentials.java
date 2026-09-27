package com.timetotrack.timetotrack.model;

/** A user plus their password hash. Never serialised: only AuthService reads it. */
public record UserCredentials(User user, String passwordHash) {
}
