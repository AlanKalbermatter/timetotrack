package com.timetotrack.timetotrack.error;

import io.vertx.core.Future;
import io.vertx.pgclient.PgException;

import java.util.function.Function;
import java.util.function.Supplier;

/** Translates Postgres constraint violations into client-facing API errors. */
public final class PgErrors {

    public static final String UNIQUE_VIOLATION = "23505";
    public static final String FOREIGN_KEY_VIOLATION = "23503";
    public static final String CHECK_VIOLATION = "23514";

    private PgErrors() {
    }

    public static boolean is(Throwable failure, String sqlState) {
        return failure instanceof PgException pg && sqlState.equals(pg.getSqlState());
    }

    /** For {@code Future.recover}: replaces a matching violation, passes anything else through untouched. */
    public static <T> Function<Throwable, Future<T>> translate(String sqlState, Supplier<ApiException> replacement) {
        return failure -> Future.failedFuture(is(failure, sqlState) ? replacement.get() : failure);
    }
}
