package com.timetotrack.timetotrack.error;

public class UnauthorizedException extends ApiException {

    public UnauthorizedException(String message) {
        super(401, message);
    }
}
