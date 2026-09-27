package com.timetotrack.timetotrack.error;

public class ConflictException extends ApiException {

    public ConflictException(String message) {
        super(409, message);
    }
}
