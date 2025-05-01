package com.timetotrack.timetotrack.service;

import com.timetotrack.timetotrack.dao.UserDao;
import com.timetotrack.timetotrack.model.User;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class UserServiceTest {

    private UserDao userDao;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userDao = mock(UserDao.class);
        userService = new UserService(userDao);
    }

    @Test
    void testCreateUserDelegatesToDao() {
        User user = new User(null, "testuser", "testuser@example.com", "Test User");

        doAnswer(invocation -> {
            Handler<AsyncResult<User>> handler = invocation.getArgument(1);
            handler.handle(Future.succeededFuture(user));
            return null;
        }).when(userDao).createUser(any(User.class), any());

        userService.createUser(user, result -> {
            assert(result.succeeded());
            assert(result.result().username.equals("testuser"));
        });

        verify(userDao, times(1)).createUser(any(User.class), any());
    }
}
