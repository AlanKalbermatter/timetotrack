package com.timetotrack.timetotrack.dao;

import com.timetotrack.timetotrack.constant.UserSQL;
import com.timetotrack.timetotrack.database.Rows;
import com.timetotrack.timetotrack.model.User;
import com.timetotrack.timetotrack.model.UserCredentials;
import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;

import java.util.List;
import java.util.Optional;

/**
 * Data access for {@code "user"} rows. Only {@link #findCredentialsByEmail} ever reads the password hash.
 */
public class UserDao {

    private final Pool pool;

    public UserDao(Pool pool) {
        this.pool = pool;
    }

    public Future<List<User>> findAll() {
        return pool.query(UserSQL.SELECT_ALL).execute().map(rows -> Rows.map(rows, UserDao::toUser));
    }

    public Future<Optional<User>> findById(int id) {
        return pool.preparedQuery(UserSQL.SELECT_BY_ID)
                .execute(Tuple.of(id))
                .map(rows -> Rows.first(rows).map(UserDao::toUser));
    }

    public Future<Optional<UserCredentials>> findCredentialsByEmail(String email) {
        return pool.preparedQuery(UserSQL.SELECT_CREDENTIALS_BY_EMAIL)
                .execute(Tuple.of(email))
                .map(rows -> Rows.first(rows).map(row -> new UserCredentials(toUser(row), row.getString("password_hash"))));
    }

    public Future<User> create(User user, String passwordHash) {
        return pool.preparedQuery(UserSQL.INSERT_ONE)
                .execute(Tuple.of(user.username(), user.email(), user.fullName(), passwordHash))
                .map(rows -> new User(rows.iterator().next().getInteger("user_id"), user.username(), user.email(), user.fullName()));
    }

    private static User toUser(Row row) {
        return new User(
                row.getInteger("user_id"),
                row.getString("username"),
                row.getString("email"),
                row.getString("full_name"));
    }
}
