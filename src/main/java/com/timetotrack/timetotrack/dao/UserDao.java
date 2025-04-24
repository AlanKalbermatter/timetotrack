package com.timetotrack.timetotrack.dao;

import com.timetotrack.timetotrack.model.User;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
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
}
