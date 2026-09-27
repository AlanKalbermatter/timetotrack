package com.timetotrack.timetotrack;

import com.timetotrack.timetotrack.config.AppConfig;
import com.timetotrack.timetotrack.database.DatabaseProvider;
import com.timetotrack.timetotrack.dependencyInjection.AppComponent;
import com.timetotrack.timetotrack.dependencyInjection.AppModule;
import com.timetotrack.timetotrack.dependencyInjection.DaggerAppComponent;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Verticle;
import io.vertx.sqlclient.Pool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/** Deploys the internal services first and the gateway last, so traffic is only accepted once upstreams are up. */
public class MainVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);

    private final AppConfig config;
    private Pool pool;

    public MainVerticle(AppConfig config) {
        this.config = config;
    }

    @Override
    public void start(Promise<Void> startPromise) {
        pool = DatabaseProvider.createPgPool(vertx, config.db());
        AppComponent component = DaggerAppComponent.builder()
                .appModule(new AppModule(config, vertx, pool))
                .build();

        List<Verticle> services = List.of(
                component.authApiVerticle(),
                component.userApiVerticle(),
                component.customerApiVerticle(),
                component.projectApiVerticle(),
                component.timeEntryApiVerticle(),
                component.docsVerticle());

        Future.all(services.stream().map(verticle -> vertx.deployVerticle(verticle)).toList())
                .compose(deployed -> vertx.deployVerticle(component.gatewayVerticle()))
                .onSuccess(id -> {
                    LOGGER.info("TimeToTrack API ready on port {} (profile {})", config.gatewayPort(), config.profile());
                    startPromise.complete();
                })
                .onFailure(startPromise::fail);
    }

    @Override
    public void stop(Promise<Void> stopPromise) {
        (pool == null ? Future.<Void>succeededFuture() : pool.close()).onComplete(done -> stopPromise.complete());
    }
}
