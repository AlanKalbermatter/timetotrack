package com.timetotrack.timetotrack.error;

/**
 * The request conflicts with existing data (duplicate, running timer, referenced row) → HTTP 409.
 */
public class ConflictException extends ApiException {

    public ConflictException(String message) {
        super(409, message);
    }
}
