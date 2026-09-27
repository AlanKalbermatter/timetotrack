package com.timetotrack.timetotrack.error;

import io.vertx.core.Future;

import java.util.Optional;

/**
 * The resource does not exist, or does not belong to the caller → HTTP 404.
 */
public class NotFoundException extends ApiException {

    public NotFoundException(String message) {
        super(404, message);
    }

    public static <T> Future<T> require(Optional<T> value, String message) {
        return value.map(Future::succeededFuture).orElseGet(() -> Future.failedFuture(new NotFoundException(message)));
    }
}
