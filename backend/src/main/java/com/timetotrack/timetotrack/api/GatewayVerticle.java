package com.timetotrack.timetotrack.api;

import com.timetotrack.timetotrack.auth.TokenService;
import com.timetotrack.timetotrack.config.ServicePorts;
import com.timetotrack.timetotrack.error.NotFoundException;
import com.timetotrack.timetotrack.error.UnauthorizedException;
import com.timetotrack.timetotrack.http.Http;
import com.timetotrack.timetotrack.http.ServiceVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * The only public listener. Matches the path prefix to an internal service, authenticates the
 * bearer token for non-public routes, and proxies the request with X-User-Id set from the token.
 * Only an allow-list of request headers is forwarded, so a client-supplied X-User-Id never reaches a service.
 */
public class GatewayVerticle extends AbstractVerticle {

    record Route(String prefix, int targetPort, boolean isPublic) {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(GatewayVerticle.class);
    private static final List<String> FORWARDED_REQUEST_HEADERS = List.of("Content-Type", "Accept");
    private static final String BEARER = "Bearer ";
    private static final long MAX_BODY_BYTES = 64 * 1024;

    private final int port;
    private final List<Route> routes;
    private final TokenService tokens;
    private WebClient client;

    public GatewayVerticle(int port, ServicePorts ports, TokenService tokens) {
        this.port = port;
        this.tokens = tokens;
        this.routes = List.of(
                new Route("/api/auth", ports.auth(), true),
                new Route("/api/docs", ports.docs(), true),
                new Route("/api/users", ports.users(), false),
                new Route("/api/customers", ports.customers(), false),
                new Route("/api/projects", ports.projects(), false),
                new Route("/api/time-entries", ports.timeEntries(), false));
    }

    Optional<Route> routeFor(String path) {
        return routes.stream()
                .filter(route -> path.equals(route.prefix()) || path.startsWith(route.prefix() + "/"))
                .max(Comparator.comparingInt(route -> route.prefix().length()));
    }

    @Override
    public void start(Promise<Void> startPromise) {
        client = WebClient.create(vertx, new WebClientOptions().setFollowRedirects(false));
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create().setBodyLimit(MAX_BODY_BYTES));
        router.route().handler(this::dispatch);

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(port, "0.0.0.0")
                .onSuccess(server -> {
                    LOGGER.info("Gateway listening on 0.0.0.0:{}", server.actualPort());
                    startPromise.complete();
                })
                .onFailure(startPromise::fail);
    }

    private void dispatch(RoutingContext ctx) {
        Optional<Route> match = routeFor(ctx.request().path());
        if (match.isEmpty()) {
            Http.error(ctx, new NotFoundException("No route for " + ctx.request().path()));
            return;
        }
        Route route = match.get();
        if (route.isPublic()) {
            forward(ctx, route, null);
            return;
        }
        String authorization = ctx.request().getHeader("Authorization");
        if (authorization == null || !authorization.startsWith(BEARER)) {
            Http.error(ctx, new UnauthorizedException("Missing bearer token"));
            return;
        }
        tokens.verify(authorization.substring(BEARER.length()).trim())
                .onSuccess(userId -> forward(ctx, route, userId))
                .onFailure(failure -> Http.error(ctx, failure));
    }

    private void forward(RoutingContext ctx, Route route, Integer userId) {
        HttpRequest<Buffer> upstream = client.request(
                ctx.request().method(), route.targetPort(), ServiceVerticle.INTERNAL_HOST, ctx.request().uri());
        for (String header : FORWARDED_REQUEST_HEADERS) {
            String value = ctx.request().getHeader(header);
            if (value != null) {
                upstream.putHeader(header, value);
            }
        }
        if (userId != null) {
            upstream.putHeader(Http.USER_ID_HEADER, String.valueOf(userId));
        }
        Buffer body = ctx.body().buffer();
        Future<HttpResponse<Buffer>> response = body == null || body.length() == 0
                ? upstream.send()
                : upstream.sendBuffer(body);
        response.onSuccess(result -> {
            ctx.response().setStatusCode(result.statusCode());
            String contentType = result.getHeader("Content-Type");
            if (contentType != null) {
                ctx.response().putHeader("Content-Type", contentType);
            }
            Buffer payload = result.body();
            if (payload == null) {
                ctx.response().end();
            } else {
                ctx.response().end(payload);
            }
        }).onFailure(failure -> {
            LOGGER.error("Upstream {} failed for {} {}", route.prefix(), ctx.request().method(), ctx.request().path(), failure);
            Http.send(ctx, 502, Http.errorBody("Upstream service unavailable"));
        });
    }
}
