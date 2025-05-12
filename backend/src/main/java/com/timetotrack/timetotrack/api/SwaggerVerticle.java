package com.timetotrack.timetotrack.api;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SwaggerVerticle extends AbstractVerticle {

    private final int port;
    private final Logger LOGGER = LoggerFactory.getLogger(SwaggerVerticle.class);

    public SwaggerVerticle(int port) {
        this.port = port;
    }

    @Override
    public void start(Promise<Void> startPromise) {
        Router router = Router.router(vertx);

        router.route("/docs/*").handler(StaticHandler.create("swagger-ui").setCachingEnabled(false));

        router.route("/docs").handler(ctx ->
                ctx.response().putHeader("Location", "/docs/index.html").setStatusCode(302).end()
        );

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(port, http -> {
                    if (http.succeeded()) {
                        LOGGER.info("Swagger UI available at http://localhost:{}/docs", port);
                        startPromise.complete();
                    } else {
                        LOGGER.error("Failed to start Swagger UI at http://localhost:{}/docs", port);
                        startPromise.fail(http.cause());
                    }
                });
    }
}