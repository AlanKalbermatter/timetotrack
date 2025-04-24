package com.timetotrack.timetotrack.dao;

import com.timetotrack.timetotrack.model.User;
import io.vertx.core.Vertx;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserDaoTest {

    private static Vertx vertx;
    private static JDBCClient jdbc;
    private static UserDao userDao;

    @BeforeAll
    static void setup() {
        vertx = Vertx.vertx();
        JsonObject config = new JsonObject()
                .put("url", "jdbc:postgresql://localhost:5432/goldentimer")
                .put("driver_class", "org.postgresql.Driver")
                .put("user", "goldentimer")
                .put("password", "goldentimer123");

        jdbc = JDBCClient.createShared(vertx, config);
        userDao = new UserDao(jdbc);
    }

    @Test
    public void fetchAll_shouldReturnUsersFromDb(TestInfo testInfo) {
        userDao.fetchAll(result -> {
            assertTrue(result.succeeded());
            List<User> users = result.result();
            assertNotNull(users);
            System.out.println("[" + testInfo.getDisplayName() + "] Usuarios cargados: " + users.size());
        });
    }
}
