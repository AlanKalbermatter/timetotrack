package com.timetotrack.timetotrack.http;

import com.timetotrack.timetotrack.error.ApiException;
import com.timetotrack.timetotrack.error.NotFoundException;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An internal service: its own HTTP server on the loopback interface, reachable only through the gateway.
 * Subclasses declare routes; this class owns body limits, the caller-identity guard and JSON 404/500s.
 */
public abstract class ServiceVerticle extends AbstractVerticle {

    public static final String INTERNAL_HOST = "127.0.0.1";

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceVerticle.class);
    private static final long MAX_BODY_BYTES = 64 * 1024;

    private final int port;

    protected ServiceVerticle(int port) {
        this.port = port;
    }

    protected abstract void routes(Router router);

    /** Whether every route needs the gateway-provided X-User-Id header. Public services (auth, docs) override. */
    protected boolean requiresUser() {
        return true;
    }

    @Override
    public void start(Promise<Void> startPromise) {
        Router router = Router.router(vertx);
        // JSON-only API: never let BodyHandler write multipart file parts to disk.
        router.route().handler(BodyHandler.create(false).setBodyLimit(MAX_BODY_BYTES));
        if (requiresUser()) {
            router.route().handler(ctx -> {
                try {
                    Http.userId(ctx);
                    ctx.next();
                } catch (ApiException e) {
                    Http.error(ctx, e);
                }
            });
        }
        routes(router);
        router.route().last().handler(ctx -> Http.error(ctx,
                new NotFoundException("No route for " + ctx.request().method() + " " + ctx.request().path())));
        router.errorHandler(500, ctx -> Http.error(ctx, ctx.failure()));

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(port, INTERNAL_HOST)
                .onSuccess(server -> {
                    LOGGER.info("{} listening on {}:{}", getClass().getSimpleName(), INTERNAL_HOST, server.actualPort());
                    startPromise.complete();
                })
                .onFailure(startPromise::fail);
    }
}
