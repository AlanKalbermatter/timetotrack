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
}
