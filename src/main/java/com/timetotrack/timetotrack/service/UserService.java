package com.timetotrack.timetotrack.service;

import com.timetotrack.timetotrack.dao.UserDao;
import com.timetotrack.timetotrack.model.User;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import java.util.List;

public class UserService {

    private final UserDao dao;

    public UserService(UserDao dao) {
        this.dao = dao;
    }

    public void getAllUsers(Handler<AsyncResult<List<User>>> resultHandler) {
        dao.fetchAll(resultHandler);
    }

    public void createUser(User user, Handler<AsyncResult<User>> resultHandler) {
        dao.createUser(user, resultHandler);
    }

    public void getUserById(long id, Handler<AsyncResult<User>> resultHandler) {
        dao.fetchById(id, resultHandler);
    }

    public void updateUser(User user, Handler<AsyncResult<User>> resultHandler) {
        dao.updateUser(user, resultHandler);
    }

    public void deleteUser(long id, Handler<AsyncResult<Void>> resultHandler) {
        dao.deleteUser(id, resultHandler);
    }

}
