package com.timetotrack.timetotrack;

import com.timetotrack.timetotrack.api.CustomerApiVerticle;
import com.timetotrack.timetotrack.api.ProjectApiVerticle;
import com.timetotrack.timetotrack.api.UserApiVerticle;
import com.timetotrack.timetotrack.database.DatabaseProvider;
import com.timetotrack.timetotrack.di.AppComponent;
import com.timetotrack.timetotrack.di.AppModule;
import com.timetotrack.timetotrack.di.DaggerAppComponent;
import io.vertx.core.*;
import io.vertx.sqlclient.Pool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) {
    Pool client = DatabaseProvider.createPgPool(vertx);
    int userPort = 8888;
    int customerPort = 8889;
    int projectPort = 8890;

    AppComponent component = DaggerAppComponent.builder()
            .appModule(new AppModule(vertx, client, userPort, customerPort, projectPort))
            .build();

    UserApiVerticle userApi = component.userApiVerticle();
    CustomerApiVerticle customerApi = component.customerApiVerticle();
    ProjectApiVerticle projectApi = component.projectApiVerticle();

    Future.all(
            vertx.deployVerticle(userApi),
            vertx.deployVerticle(customerApi),
            vertx.deployVerticle(projectApi)
    ).onComplete(ar -> {
      if (ar.succeeded()) {
        LOGGER.info("✅ All API verticles deployed successfully");
        startPromise.complete();
      } else {
        LOGGER.error("❌ Failed to deploy one or more API verticles", ar.cause());
        startPromise.fail(ar.cause());
      }
    });
  }
}
