package com.timetotrack.timetotrack.dao;

import com.timetotrack.timetotrack.constant.UserSQL;
import com.timetotrack.timetotrack.model.User;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserDao {

    private final Pool client;

    public UserDao(Pool client) {
        this.client = client;
    }

    public void fetchAll(Handler<AsyncResult<List<User>>> resultHandler) {
        client.query(UserSQL.SELECT_ALL)
                .execute(ar -> {
                    if (ar.succeeded()) {
                        List<User> users = new ArrayList<>();
                        for (Row row : ar.result()) {
                            users.add(mapRowToUser(row));
                        }
                        resultHandler.handle(Future.succeededFuture(users));
                    } else {
                        resultHandler.handle(Future.failedFuture(ar.cause()));
                    }
                });
    }

    public void createUser(User user, Handler<AsyncResult<User>> resultHandler) {
        client.preparedQuery(UserSQL.INSERT_ONE)
                .execute(Tuple.of(user.getUsername(), user.getEmail(), user.getFullName()), ar -> {
                    if (ar.succeeded()) {
                        Row row = ar.result().iterator().next();
                        user.setId(row.getInteger("user_id"));
                        resultHandler.handle(Future.succeededFuture(user));
                    } else {
                        ar.cause().printStackTrace();
                        resultHandler.handle(Future.failedFuture(ar.cause()));

                    }
                });
    }

    public void fetchById(long id, Handler<AsyncResult<User>> resultHandler) {
        client.preparedQuery(UserSQL.SELECT_BY_ID)
                .execute(Tuple.of(id), ar -> {
                    if (ar.succeeded()) {
                        if (ar.result().rowCount() > 0) {
                            Row row = ar.result().iterator().next();
                            User user = mapRowToUser(row);
                            resultHandler.handle(Future.succeededFuture(user));
                        } else {
                            resultHandler.handle(Future.failedFuture("User not found with id: " + id));
                        }
                    } else {
                        resultHandler.handle(Future.failedFuture(ar.cause()));
                    }
                });
    }

    public void updateUser(User user, Handler<AsyncResult<User>> resultHandler) {
        client.preparedQuery(UserSQL.UPDATE_ONE)
                .execute(Tuple.of(
                        user.getUsername(),
                        user.getEmail(),
                        user.getFullName(),
                        user.getId()), ar -> {
                    if (ar.succeeded()) {
                        resultHandler.handle(Future.succeededFuture(user));
                    } else {
                        resultHandler.handle(Future.failedFuture(ar.cause()));
                    }
                });
    }

    public void deleteUser(long id, Handler<AsyncResult<Void>> resultHandler) {
        client.preparedQuery(UserSQL.DELETE_BY_ID)
                .execute(Tuple.of(id), ar -> {
                    if (ar.succeeded()) {
                        resultHandler.handle(Future.succeededFuture());
                    } else {
                        resultHandler.handle(Future.failedFuture(ar.cause()));
                    }
                });
    }

    private User mapRowToUser(Row row) {
        return new User(
                row.getInteger("user_id"),
                row.getString("username"),
                row.getString("email"),
                row.getString("full_name")
        );
    }
}
