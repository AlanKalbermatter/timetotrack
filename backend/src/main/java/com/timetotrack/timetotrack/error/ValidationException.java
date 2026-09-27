package com.timetotrack.timetotrack.error;

/**
 * Invalid input → HTTP 400.
 */
public class ValidationException extends ApiException {

    public ValidationException(String message) {
        super(400, message);
    }
}
