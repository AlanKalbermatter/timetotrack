/*
 * Copyright (c) 2022. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package timetotrack.microservice.timer.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import timetotrack.microservice.timer.Timer;
import timetotrack.microservice.timer.TimerService;

import java.util.List;

public class TimerServiceImpl implements TimerService {

    private static final int PAGE_LIMIT = 10;

//    public TimerServiceImpl(Vertx vertx, JsonObject config) {
//        super(vertx, config);
//    }

    @Override
    public TimerService initializePersistence(Handler<AsyncResult<Void>> resultHandler) {
        client.getConnection(connHandler(resultHandler, connection -> {
            connection.execute(CREATE_STATEMENT, r -> {
                resultHandler.handle(r);
                connection.close();
            });
        }));
        return this;
    }

    @Override
    public TimerService addTimer(Timer timer, Handler<AsyncResult<Void>> resultHandler) {
        return null;
    }

    @Override
    public TimerService retrieveTimer(Long timerId, Handler<AsyncResult<Timer>> resultHandler) {
        return null;
    }

    @Override
    public TimerService retrieveAllTimers(Handler<AsyncResult<List<Timer>>> resultHandler) {
        return null;
    }

    @Override
    public TimerService deleteTimer(Long timerId, Handler<AsyncResult<Void>> resultHandler) {
        return null;
    }

    @Override
    public TimerService deleteAllTimers(Handler<AsyncResult<Void>> resultHandler) {
        return null;
    }
}
