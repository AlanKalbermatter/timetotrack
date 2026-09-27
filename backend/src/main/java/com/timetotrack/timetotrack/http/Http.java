package com.timetotrack.timetotrack.http;

import com.timetotrack.timetotrack.error.ApiException;
import com.timetotrack.timetotrack.error.UnauthorizedException;
import com.timetotrack.timetotrack.error.ValidationException;
import io.vertx.core.Future;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/** Request parsing and response writing shared by every service verticle. */
public final class Http {

    /** Set by the gateway from the validated JWT; internal services trust nothing else. */
    public static final String USER_ID_HEADER = "X-User-Id";

    private static final Logger LOGGER = LoggerFactory.getLogger(Http.class);

    private Http() {
    }

    /**
     * Runs {@code action} and writes its result: JsonObject/JsonArray with {@code successStatus}, null as 204.
     * Synchronous exceptions (e.g. validation while parsing) and failed futures both go through {@link #error}.
     */
    public static void respond(RoutingContext ctx, int successStatus, Supplier<Future<?>> action) {
        Future<?> result;
        try {
            result = action.get();
        } catch (RuntimeException e) {
            error(ctx, e);
            return;
        }
        result.onSuccess(value -> {
            if (value == null) {
                ctx.response().setStatusCode(204).end();
            } else if (value instanceof JsonObject json) {
                send(ctx, successStatus, json.encode());
            } else if (value instanceof JsonArray json) {
                send(ctx, successStatus, json.encode());
            } else {
                error(ctx, new IllegalStateException("Unsupported response type " + value.getClass().getName()));
            }
        }).onFailure(failure -> error(ctx, failure));
    }

    public static void error(RoutingContext ctx, Throwable failure) {
        if (ctx.response().ended()) {
            return;
        }
        if (failure instanceof ApiException api) {
            send(ctx, api.status(), errorBody(api.getMessage()));
            return;
        }
        LOGGER.error("Unhandled error on {} {}", ctx.request().method(), ctx.request().path(), failure);
        send(ctx, 500, errorBody("Internal server error"));
    }

    public static void send(RoutingContext ctx, int status, String json) {
        ctx.response()
                .setStatusCode(status)
                .putHeader("Content-Type", "application/json")
                .end(json);
    }

    public static String errorBody(String message) {
        return new JsonObject().put("error", message).encode();
    }

    public static int userId(RoutingContext ctx) {
        String header = ctx.request().getHeader(USER_ID_HEADER);
        if (header == null) {
            throw new UnauthorizedException("Missing authenticated user");
        }
        try {
            return Integer.parseInt(header);
        } catch (NumberFormatException e) {
            throw new UnauthorizedException("Invalid authenticated user");
        }
    }

    public static long pathId(RoutingContext ctx, String name) {
        String raw = ctx.pathParam(name);
        try {
            long id = Long.parseLong(raw);
            if (id > 0) {
                return id;
            }
        } catch (NumberFormatException ignored) {
            // falls through to the validation error below
        }
        throw new ValidationException("Invalid " + name + ": " + raw);
    }

    public static int pathIntId(RoutingContext ctx, String name) {
        long id = pathId(ctx, name);
        if (id > Integer.MAX_VALUE) {
            throw new ValidationException("Invalid " + name + ": " + id);
        }
        return (int) id;
    }

    public static JsonObject body(RoutingContext ctx) {
        try {
            JsonObject body = ctx.body().asJsonObject();
            if (body != null) {
                return body;
            }
        } catch (DecodeException | ClassCastException ignored) {
            // falls through to the validation error below
        }
        throw new ValidationException("Request body must be a JSON object");
    }

    public static String queryParam(RoutingContext ctx, String name) {
        String raw = ctx.request().getParam(name);
        return raw == null || raw.isBlank() ? null : raw.trim();
    }

    public static Instant queryInstant(RoutingContext ctx, String name) {
        String raw = queryParam(ctx, name);
        return raw == null ? null : JsonFields.parseInstant(name, raw);
    }

    public static <T> JsonArray toArray(List<T> items, Function<T, JsonObject> toJson) {
        return new JsonArray(items.stream().map(toJson).toList());
    }
}
