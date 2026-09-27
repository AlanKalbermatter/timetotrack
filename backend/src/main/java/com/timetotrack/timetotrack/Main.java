package com.timetotrack.timetotrack;

import com.timetotrack.timetotrack.config.AppConfig;
import io.vertx.core.Vertx;
import org.slf4j.LoggerFactory;

public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");
        AppConfig config = AppConfig.fromEnv(System.getenv());
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new MainVerticle(config)).onFailure(failure -> {
            LoggerFactory.getLogger(Main.class).error("Startup failed", failure);
            vertx.close().onComplete(closed -> System.exit(1));
        });
    }
}
