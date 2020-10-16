package com.alan.dev.timetotrack;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.web.RoutingContext;

public class DBClient {

    private JDBCClient jdbcClient;

    public DBClient(JDBCClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public void runQuery(RoutingContext routingContext, String sql) {
        jdbcClient.getConnection(ar -> {
            SQLConnection connection = ar.result();
            connection.query(sql, result -> {

                routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                        .end(result.result().getResults().toString());
                connection.close();
            });
        });
    }

    public void add(RoutingContext routingContext, String sql, JsonArray params) {
        jdbcClient.getConnection(ar -> {
          final Project project = Json.decodeValue(routingContext.getBodyAsString(), Project.class);
          jdbcClient.updateWithParams(sql, params,
          r-> routingContext.response()
          .setStatusCode(201)
          .putHeader("content-type", "application/json; charset=utf-8")
          .end(Json.encodePrettily(project)));
        });
    }

    public void runForOne(RoutingContext routingContext, String sql, JsonArray params) {
        final String id = routingContext.request().getParam("projects_id");
        if (id == null) {
            routingContext.response().setStatusCode(400).end();
        } else {
            jdbcClient.queryWithParams(sql, params, ar -> {
                if (ar.succeeded()) {
                    if (ar.result() == null) {
                        routingContext.response().setStatusCode(404).end();
                        return;
                    }
                    Project project = new Project(ar.result());
                    routingContext.response()
                            .setStatusCode(200)
                            .putHeader("content-type", "application/json; charset=utf-8")
                            .end(Json.encodePrettily(project));
                }
            });
        }
    }

    public void update(RoutingContext routingContext, String sql, JsonArray params) {
        jdbcClient.getConnection(ar -> {
            SQLConnection connection = ar.result();
            connection.updateWithParams(sql,
                    params,
                    res ->
                    routingContext.response()
                    .setStatusCode(201)
                        .putHeader("content-type", "application/json; charset=utf-8")
                    .end());
        });

    }

    public void delete(RoutingContext routingContext, String sql, JsonArray params) {
        String id = routingContext.request().getParam("id");
        if (id == null) {
            routingContext.response().setStatusCode(400).end();
        } else {
            jdbcClient.updateWithParams(sql,
                    params,
                    ar -> routingContext.response().setStatusCode(204).end());

        }
    }
}