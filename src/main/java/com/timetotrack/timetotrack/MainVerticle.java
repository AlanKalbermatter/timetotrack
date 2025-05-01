package com.timetotrack.timetotrack;

import com.timetotrack.timetotrack.api.UserApiVerticle;
import com.timetotrack.timetotrack.dao.UserDao;
import com.timetotrack.timetotrack.database.DatabaseProvider;
import com.timetotrack.timetotrack.service.UserService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.sqlclient.Pool;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) {
    Pool client = DatabaseProvider.createPgPool(vertx);

    UserDao userDao = new UserDao(client);
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
