package com.timetotrack.timetotrack.api;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.core.MultiMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GatewayVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(GatewayVerticle.class);

    @Override
    public void start(Promise<Void> startPromise) {
        Router router = Router.router(vertx);
        WebClient client = WebClient.create(vertx);

        router.route().handler(BodyHandler.create());

        proxy(router, client, "/api/users/*", 8888);
        proxy(router, client, "/api/customers/*", 8889);
        proxy(router, client, "/api/projects/*", 8890);
        proxy(router, client, "/api/time-entries/*", 8891);

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(3000, http -> {
                    if (http.succeeded()) {
                        LOGGER.info("API Gateway running on http://localhost:3014");
                        startPromise.complete();
                    } else {
                        LOGGER.error("API Gateway running failed", http.cause());
                        startPromise.fail(http.cause());
                    }
                });
    }

    private void proxy(Router router, WebClient client, String path, int targetPort) {
        router.route(path).handler(ctx -> {
            String targetPath = ctx.request().uri();

            ctx.request().body().onComplete(bodyResult -> {
                if (bodyResult.failed()) {
                    LOGGER.error(bodyResult.cause().getMessage(), bodyResult.cause());
                    ctx.response().setStatusCode(400).end("Failed to read request body");
                    return;
                }

                Buffer body = bodyResult.result();

                client.requestAbs(ctx.request().method(), "http://localhost:" + targetPort + targetPath)
                        .putHeaders(ctx.request().headers())
                        .sendBuffer(body, ar -> {
                            if (ar.succeeded()) {
                                LOGGER.info("HTTP response sent to {}", targetPath);
                                HttpResponse<Buffer> response = ar.result();

                                ctx.response().setStatusCode(response.statusCode());

                                MultiMap headers = response.headers();
                                headers.forEach(header -> ctx.response().putHeader(header.getKey(), header.getValue()));

                                ctx.response().end(response.body());
                            } else {
                                LOGGER.error(ar.cause().getMessage(), ar.cause());
                                ar.cause().printStackTrace();
                                ctx.response().setStatusCode(502).end("Gateway error");
                            }
                        });
            });
        });
    }
}