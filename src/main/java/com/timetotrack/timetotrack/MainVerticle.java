package com.timetotrack.timetotrack;

import com.timetotrack.timetotrack.api.UserApiVerticle;
import com.timetotrack.timetotrack.dao.UserDao;
import com.timetotrack.timetotrack.service.UserService;
import io.vertx.core.*;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.core.json.JsonObject;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) {
    JsonObject dbConfig = new JsonObject()
            .put("url", "jdbc:postgresql://localhost:5432/goldentimer")
            .put("driver_class", "org.postgresql.Driver")
            .put("user", "goldentimer")
            .put("password", "goldentimer123");

    JDBCClient jdbc = JDBCClient.createShared(vertx, dbConfig);
    UserDao userDao = new UserDao(jdbc);
    UserService userService = new UserService(userDao);

    UserApiVerticle userApi = new UserApiVerticle(userService);

    vertx.deployVerticle(userApi, res -> {
      if (res.succeeded()) {
        startPromise.complete();
      } else {
        startPromise.fail(res.cause());
      }
    });
  }
}
