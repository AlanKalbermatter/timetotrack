package com.timetotrack.timetotrack.error;

/**
 * Missing, invalid or expired credentials → HTTP 401.
 */
public class UnauthorizedException extends ApiException {

    public UnauthorizedException(String message) {
        super(401, message);
    }
}
