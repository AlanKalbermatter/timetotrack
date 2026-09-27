package com.timetotrack.timetotrack.service;

import com.timetotrack.timetotrack.dao.UserDao;
import com.timetotrack.timetotrack.error.NotFoundException;
import com.timetotrack.timetotrack.model.User;
import io.vertx.core.Future;

import java.util.List;

public class UserService {

    private final UserDao users;

    public UserService(UserDao users) {
        this.users = users;
    }

    public Future<List<User>> findAll() {
        return users.findAll();
    }

    public Future<User> findById(int id) {
        return users.findById(id).compose(found -> NotFoundException.require(found, "User " + id + " not found"));
    }
}
