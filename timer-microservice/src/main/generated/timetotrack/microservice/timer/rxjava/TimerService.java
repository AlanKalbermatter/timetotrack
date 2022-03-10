/*
 * Copyright 2014 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package timetotrack.microservice.timer.rxjava;

import rx.Observable;
import rx.Single;
import io.vertx.rx.java.RxHelper;
import io.vertx.rx.java.WriteStreamSubscriber;
import io.vertx.rx.java.SingleOnSubscribeAdapter;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Collectors;
import io.vertx.core.Handler;
import io.vertx.core.AsyncResult;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import io.vertx.lang.rx.RxGen;
import io.vertx.lang.rx.TypeArg;
import io.vertx.lang.rx.MappingIterator;


@RxGen(timetotrack.microservice.timer.TimerService.class)
public class TimerService {

  @Override
  public String toString() {
    return delegate.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TimerService that = (TimerService) o;
    return delegate.equals(that.delegate);
  }
  
  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  public static final TypeArg<TimerService> __TYPE_ARG = new TypeArg<>(    obj -> new TimerService((timetotrack.microservice.timer.TimerService) obj),
    TimerService::getDelegate
  );

  private final timetotrack.microservice.timer.TimerService delegate;
  
  public TimerService(timetotrack.microservice.timer.TimerService delegate) {
    this.delegate = delegate;
  }

  public TimerService(Object delegate) {
    this.delegate = (timetotrack.microservice.timer.TimerService)delegate;
  }

  public timetotrack.microservice.timer.TimerService getDelegate() {
    return delegate;
  }

  public timetotrack.microservice.timer.rxjava.TimerService initializePersistence(io.vertx.core.Handler<io.vertx.core.AsyncResult<java.lang.Void>> resultHandler) { 
    delegate.initializePersistence(resultHandler);
    return this;
  }

  public timetotrack.microservice.timer.rxjava.TimerService initializePersistence() {
    return 
initializePersistence(ar -> { });
  }

    public rx.Single<java.lang.Void> rxInitializePersistence() { 
    return Single.create(new SingleOnSubscribeAdapter<>(fut -> {
      initializePersistence(fut);
    }));
  }

  public timetotrack.microservice.timer.rxjava.TimerService addTimer(timetotrack.microservice.timer.Timer timer, io.vertx.core.Handler<io.vertx.core.AsyncResult<java.lang.Void>> resultHandler) { 
    delegate.addTimer(timer, resultHandler);
    return this;
  }

  public timetotrack.microservice.timer.rxjava.TimerService addTimer(timetotrack.microservice.timer.Timer timer) {
    return 
addTimer(timer, ar -> { });
  }

    public rx.Single<java.lang.Void> rxAddTimer(timetotrack.microservice.timer.Timer timer) { 
    return Single.create(new SingleOnSubscribeAdapter<>(fut -> {
      addTimer(timer, fut);
    }));
  }

  public timetotrack.microservice.timer.rxjava.TimerService retrieveTimer(java.lang.Long timerId, io.vertx.core.Handler<io.vertx.core.AsyncResult<timetotrack.microservice.timer.Timer>> resultHandler) { 
    delegate.retrieveTimer(timerId, resultHandler);
    return this;
  }

  public timetotrack.microservice.timer.rxjava.TimerService retrieveTimer(java.lang.Long timerId) {
    return 
retrieveTimer(timerId, ar -> { });
  }

    public rx.Single<timetotrack.microservice.timer.Timer> rxRetrieveTimer(java.lang.Long timerId) { 
    return Single.create(new SingleOnSubscribeAdapter<>(fut -> {
      retrieveTimer(timerId, fut);
    }));
  }

  public timetotrack.microservice.timer.rxjava.TimerService retrieveAllTimers(io.vertx.core.Handler<io.vertx.core.AsyncResult<java.util.List<timetotrack.microservice.timer.Timer>>> resultHandler) { 
    delegate.retrieveAllTimers(resultHandler);
    return this;
  }

  public timetotrack.microservice.timer.rxjava.TimerService retrieveAllTimers() {
    return 
retrieveAllTimers(ar -> { });
  }

    public rx.Single<java.util.List<timetotrack.microservice.timer.Timer>> rxRetrieveAllTimers() { 
    return Single.create(new SingleOnSubscribeAdapter<>(fut -> {
      retrieveAllTimers(fut);
    }));
  }

  public timetotrack.microservice.timer.rxjava.TimerService deleteTimer(java.lang.Long timerId, io.vertx.core.Handler<io.vertx.core.AsyncResult<java.lang.Void>> resultHandler) { 
    delegate.deleteTimer(timerId, resultHandler);
    return this;
  }

  public timetotrack.microservice.timer.rxjava.TimerService deleteTimer(java.lang.Long timerId) {
    return 
deleteTimer(timerId, ar -> { });
  }

    public rx.Single<java.lang.Void> rxDeleteTimer(java.lang.Long timerId) { 
    return Single.create(new SingleOnSubscribeAdapter<>(fut -> {
      deleteTimer(timerId, fut);
    }));
  }

  public timetotrack.microservice.timer.rxjava.TimerService deleteAllTimers(io.vertx.core.Handler<io.vertx.core.AsyncResult<java.lang.Void>> resultHandler) { 
    delegate.deleteAllTimers(resultHandler);
    return this;
  }

  public timetotrack.microservice.timer.rxjava.TimerService deleteAllTimers() {
    return 
deleteAllTimers(ar -> { });
  }

    public rx.Single<java.lang.Void> rxDeleteAllTimers() { 
    return Single.create(new SingleOnSubscribeAdapter<>(fut -> {
      deleteAllTimers(fut);
    }));
  }

  public static final java.lang.String SERVICE_NAME = timetotrack.microservice.timer.TimerService.SERVICE_NAME;
  public static final java.lang.String SERVICE_ADDRESS = timetotrack.microservice.timer.TimerService.SERVICE_ADDRESS;
  public static TimerService newInstance(timetotrack.microservice.timer.TimerService arg) {
    return arg != null ? new TimerService(arg) : null;
  }

}
