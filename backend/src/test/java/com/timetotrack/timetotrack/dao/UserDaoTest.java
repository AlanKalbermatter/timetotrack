package com.timetotrack.timetotrack.dao;

import com.timetotrack.timetotrack.database.DatabaseProvider;
import com.timetotrack.timetotrack.model.User;
import io.vertx.core.Vertx;
import io.vertx.sqlclient.Pool;
import org.junit.jupiter.api.*;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class UserDaoTest {

    private static Vertx vertx;
    private static Pool client;
    private static UserDao userDao;

    @BeforeAll
    static void setup() {
        vertx = Vertx.vertx();
        client = DatabaseProvider.createPgPool(vertx);
        userDao = new UserDao(client);
    }

    @Test
    public void fetchAllShouldReturnUsersFromDb(TestInfo testInfo) {
        userDao.fetchAll(result -> {
            assertTrue(result.succeeded());
            List<User> users = result.result();
            assertNotNull(users);
            System.out.println("[" + testInfo.getDisplayName() + "] Usuarios cargados: " + users.size());
        });
    }

    @Test
    public void createUserShouldSaveUserInDatabase(TestInfo testInfo) {
        User user = new User(null, "testuser_" + System.currentTimeMillis(), "test@mail.com", "Test User");

        userDao.createUser(user, res -> {
            assertTrue(res.succeeded());
            User saved = res.result();
            assertNotNull(saved.id);
            System.out.println("[" + testInfo.getDisplayName() + "] Inserted user ID: " + saved.id);
        });
    }

    @AfterAll
    public static void tearDown() {
        vertx.close();
    }
}
