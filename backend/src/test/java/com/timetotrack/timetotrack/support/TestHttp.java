package com.timetotrack.timetotrack.support;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/** Blocking HTTP client for tests. Immutable: the with*() methods return a copy. */
public final class TestHttp {

    public record Response(int status, String body) {

        public JsonObject json() {
            return new JsonObject(body);
        }

        public JsonArray jsonArray() {
            return new JsonArray(body);
        }

        public String error() {
            return json().getString("error");
        }
    }

    private static final HttpClient CLIENT = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();

    private final int port;
    private final Map<String, String> headers;

    public TestHttp(int port) {
        this(port, Map.of());
    }

    private TestHttp(int port, Map<String, String> headers) {
        this.port = port;
        this.headers = Map.copyOf(headers);
    }

    public TestHttp withHeader(String name, String value) {
        Map<String, String> copy = new HashMap<>(headers);
        copy.put(name, value);
        return new TestHttp(port, copy);
    }

    public TestHttp asUser(int userId) {
        return withHeader("X-User-Id", String.valueOf(userId));
    }

    public TestHttp withToken(String token) {
        return withHeader("Authorization", "Bearer " + token);
    }

    public Response get(String path) {
        return send("GET", path, null);
    }

    public Response delete(String path) {
        return send("DELETE", path, null);
    }

    public Response post(String path, JsonObject body) {
        return send("POST", path, body == null ? null : body.encode());
    }

    public Response put(String path, JsonObject body) {
        return send("PUT", path, body.encode());
    }

    public Response send(String method, String path, String rawBody) {
        return send(method, path, "application/json", rawBody);
    }

    public Response send(String method, String path, String contentType, String rawBody) {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + path))
                .timeout(Duration.ofSeconds(15))
                .method(method, rawBody == null
                        ? HttpRequest.BodyPublishers.noBody()
                        : HttpRequest.BodyPublishers.ofString(rawBody));
        if (rawBody != null) {
            builder.header("Content-Type", contentType);
        }
        headers.forEach(builder::header);
        try {
            HttpResponse<String> response = CLIENT.send(builder.build(), HttpResponse.BodyHandlers.ofString());
            return new Response(response.statusCode(), response.body());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }
}
