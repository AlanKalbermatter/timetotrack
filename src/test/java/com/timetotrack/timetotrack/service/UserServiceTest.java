package com.timetotrack.timetotrack.service;

import com.timetotrack.timetotrack.dao.UserDao;
import com.timetotrack.timetotrack.model.User;
import io.vertx.core.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserDao userDao;

    private Vertx vertx;
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        vertx = Vertx.vertx();
        userService = new UserService(userDao);
    }

    @Test
    public void shouldReturnUsersSuccessfully() {
        List<User> mockUsers = Arrays.asList(
                new User(1, "juan", "juan@test.com", "Juan Pérez"),
                new User(2, "maria", "maria@test.com", "María Gómez")
        );

        doAnswer(invocation -> {
            Handler<AsyncResult<List<User>>> handler = invocation.getArgument(0);
            handler.handle(Future.succeededFuture(mockUsers));
            return null;
        }).when(userDao).fetchAll(any());

        userService.getAllUsers(result -> {
            assertTrue(result.succeeded());
            assertEquals(2, result.result().size());
            assertEquals("juan", result.result().get(0).username);
        });
    }

    @Test
    public void shouldHandleDaoError() {
        doAnswer(invocation -> {
            Handler<AsyncResult<List<User>>> handler = invocation.getArgument(0);
            handler.handle(Future.failedFuture(new RuntimeException("DB Error")));
            return null;
        }).when(userDao).fetchAll(any());

        userService.getAllUsers(result -> {
            assertTrue(result.failed());
            assertEquals("DB Error", result.cause().getMessage());
        });
    }

    @Test
    public void shouldCreateUserSuccessfully() {
        User inputUser = new User(null, "lucas", "lucas@test.com", "Lucas Ramirez");
        User savedUser = new User(42, "lucas", "lucas@test.com", "Lucas Ramirez");

        doAnswer(invocation -> {
            Handler<AsyncResult<User>> handler = invocation.getArgument(1);
            handler.handle(Future.succeededFuture(savedUser));
            return null;
        }).when(userDao).createUser(eq(inputUser), any());

        userService.createUser(inputUser, res -> {
            assertTrue(res.succeeded());
            assertEquals("lucas", res.result().username);
            assertEquals(42, res.result().id);
        });
    }
}

