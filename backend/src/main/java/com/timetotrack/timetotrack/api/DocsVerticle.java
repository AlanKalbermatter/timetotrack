package com.timetotrack.timetotrack.api;

import com.timetotrack.timetotrack.http.ServiceVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.Router;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

/** Serves Swagger UI (loaded from a CDN) and the OpenAPI document. Public. */
public class DocsVerticle extends ServiceVerticle {

    private final Buffer indexHtml = classpathResource("docs/index.html");
    private final Buffer openApiYaml = classpathResource("openapi.yaml");

    public DocsVerticle(int port) {
        super(port);
    }

    @Override
    protected boolean requiresUser() {
        return false;
    }

    @Override
    protected void routes(Router router) {
        router.get("/api/docs").handler(ctx -> ctx.response()
                .putHeader("Content-Type", "text/html; charset=utf-8").end(indexHtml));
        router.get("/api/docs/").handler(ctx -> ctx.response()
                .putHeader("Content-Type", "text/html; charset=utf-8").end(indexHtml));
        router.get("/api/docs/openapi.yaml").handler(ctx -> ctx.response()
                .putHeader("Content-Type", "application/yaml").end(openApiYaml));
    }

    private static Buffer classpathResource(String path) {
        try (InputStream is = DocsVerticle.class.getClassLoader().getResourceAsStream(path)) {
            if (is == null) {
                throw new IllegalStateException(path + " not found on the classpath");
            }
            return Buffer.buffer(is.readAllBytes());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
