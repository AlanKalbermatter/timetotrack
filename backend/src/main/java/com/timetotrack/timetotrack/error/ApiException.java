package com.timetotrack.timetotrack.error;

/** A failure whose message is safe to show to API clients, mapped to an HTTP status. */
public class ApiException extends RuntimeException {

    private final int status;

    public ApiException(int status, String message) {
        super(message);
        this.status = status;
    }

    public int status() {
        return status;
    }
}
