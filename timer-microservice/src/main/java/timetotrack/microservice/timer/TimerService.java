package timetotrack.microservice.timer;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import java.util.List;

@VertxGen
@ProxyGen
public interface TimerService {

    String SERVICE_NAME = "timer-eb-service";

    String SERVICE_ADDRESS = "service.timer";

    @Fluent
    TimerService initializePersistence(Handler<AsyncResult<Void>> resultHandler);

    //Create a tiner
    @Fluent
    TimerService addTimer(Timer timer, Handler<AsyncResult<Void>> resultHandler);

    //Get one by id
    @Fluent
    TimerService retrieveTimer(Long timerId, Handler<AsyncResult<Timer>> resultHandler);

    //Get all
    @Fluent
    TimerService retrieveAllTimers(Handler<AsyncResult<List<Timer>>> resultHandler);

    //Delete one by id
    @Fluent
    TimerService deleteTimer(Long timerId, Handler<AsyncResult<Void>> resultHandler);

    //Delete all
    @Fluent
    TimerService deleteAllTimers(Handler<AsyncResult<Void>> resultHandler);
}
