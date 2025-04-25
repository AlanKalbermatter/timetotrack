package com.timetotrack.timetotrack.dao;

import com.timetotrack.timetotrack.model.User;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLConnection;

import java.util.List;
import java.util.stream.Collectors;

public class UserDao {

    private final JDBCClient jdbc;

    public UserDao(JDBCClient jdbc) {
        this.jdbc = jdbc;
    }

    public void fetchAll(Handler<AsyncResult<List<User>>> resultHandler) {
        jdbc.getConnection(ar -> {
            if (ar.failed()) {
                resultHandler.handle(Future.failedFuture(ar.cause()));
                return;
            }

            SQLConnection conn = ar.result();
            conn.query("SELECT user_id, username, email, full_name FROM \"user\"", res -> {
                if (res.succeeded()) {
                    List<User> users = res.result().getResults().stream().map(row -> new User(
                            row.getInteger(0),
                            row.getString(1),
                            row.getString(2),
                            row.getString(3)
                    )).collect(Collectors.toList());
                    resultHandler.handle(Future.succeededFuture(users));
                } else {
                    resultHandler.handle(Future.failedFuture(res.cause()));
                }
                conn.close();
            });
        });
    }

    public void createUser(User user, Handler<AsyncResult<User>> resultHandler) {
        String sql = "INSERT INTO \"user\" (username, email, full_name) VALUES (?, ?, ?) RETURNING user_id";

        jdbc.getConnection(ar -> {
            if (ar.failed()) {
                resultHandler.handle(Future.failedFuture(ar.cause()));
                return;
            }

            SQLConnection conn = ar.result();
            JsonArray params = new JsonArray()
                    .add(user.username)
                    .add(user.email)
                    .add(user.fullName);

            conn.queryWithParams(sql, params, res -> {
                if (res.succeeded()) {
                    List<JsonArray> results = res.result().getResults();
                    if (!results.isEmpty()) {
                        int generatedId = results.get(0).getInteger(0);
                        user.id = generatedId;
                        resultHandler.handle(Future.succeededFuture(user));
                    } else {
                        System.out.println("INSERT succeeded but no ID returned!");
                        resultHandler.handle(Future.failedFuture("Insert did not return ID"));
                    }
                } else {
                    res.cause().printStackTrace();
                    resultHandler.handle(Future.failedFuture(res.cause()));
                }
                conn.close();
            });
        });


    }
}
