package com.timetotrack.timetotrack.support;

import io.vertx.core.Future;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/** Blocks a test thread on a Vert.x Future. Never call from an event loop. */
public final class Await {

    private static final long TIMEOUT_SECONDS = 15;

    private Await() {
    }

    public static <T> T await(Future<T> future) {
        try {
            return future.toCompletionStage().toCompletableFuture().get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof RuntimeException runtime) {
                throw runtime;
            }
            throw new IllegalStateException(e.getCause());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        } catch (TimeoutException e) {
            throw new IllegalStateException("Future did not complete within " + TIMEOUT_SECONDS + "s", e);
        }
    }

    public static Throwable awaitFailure(Future<?> future) {
        try {
            future.toCompletionStage().toCompletableFuture().get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            return e.getCause();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        } catch (TimeoutException e) {
            throw new IllegalStateException("Future did not complete within " + TIMEOUT_SECONDS + "s", e);
        }
        throw new AssertionError("Expected the future to fail, but it succeeded");
    }
}
