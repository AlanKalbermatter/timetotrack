package com.timetotrack.timetotrack.database;

import com.timetotrack.timetotrack.config.DbConfig;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgBuilder;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;

/**
 * Builds the shared reactive Postgres connection pool used by every DAO.
 */
public final class DatabaseProvider {

    private DatabaseProvider() {
    }

    public static Pool createPgPool(Vertx vertx, DbConfig db) {
        PgConnectOptions connectOptions = new PgConnectOptions()
                .setHost(db.host())
                .setPort(db.port())
                .setDatabase(db.database())
                .setUser(db.user())
                .setPassword(db.password());

        return PgBuilder.pool()
                .with(new PoolOptions().setMaxSize(db.maxPoolSize()))
                .connectingTo(connectOptions)
                .using(vertx)
                .build();
    }
}
