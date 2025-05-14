package com.timetotrack.timetotrack;

import com.timetotrack.timetotrack.api.*;
import com.timetotrack.timetotrack.database.DatabaseProvider;
import com.timetotrack.timetotrack.dependencyInjection.AppComponent;
import com.timetotrack.timetotrack.dependencyInjection.AppModule;
import com.timetotrack.timetotrack.dependencyInjection.DaggerAppComponent;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
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
        int timeEntryPort = 8891;
        int swaggerPort = 8892;

        AppComponent component = DaggerAppComponent.builder()
                .appModule(new AppModule(vertx, client, userPort, customerPort, projectPort, timeEntryPort, swaggerPort))
                .build();

        UserApiVerticle userApi = component.userApiVerticle();
        CustomerApiVerticle customerApi = component.customerApiVerticle();
        ProjectApiVerticle projectApi = component.projectApiVerticle();
        TimeEntryApiVerticle timeEntryApi = component.timeEntryApiVerticle();
        SwaggerVerticle swaggerVerticle = component.swaggerVerticle();
        GatewayVerticle gatewayVerticle = component.gatewayVerticle();

        Future.all(
                vertx.deployVerticle(userApi),
                vertx.deployVerticle(customerApi),
                vertx.deployVerticle(projectApi),
                vertx.deployVerticle(timeEntryApi),
                vertx.deployVerticle(swaggerVerticle),
                vertx.deployVerticle(gatewayVerticle)
        ).onComplete(ar -> {
            if (ar.succeeded()) {
                LOGGER.info("All API verticles deployed successfully");
                startPromise.complete();
            } else {
                LOGGER.error("Failed to deploy one or more API verticles", ar.cause());
                startPromise.fail(ar.cause());
            }
        });
    }
}
