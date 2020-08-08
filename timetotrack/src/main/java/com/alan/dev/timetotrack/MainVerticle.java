package com.alan.dev.timetotrack;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.rxjava.core.Future;

public class MainVerticle extends AbstractVerticle {
  private ProjectControl projectControl = new ProjectControl();
  private CustomerControl customerControl = new CustomerControl();
  private Cap_userControl cap_userControl = new Cap_userControl();
  private Time_entryControl time_entryControl = new Time_entryControl();

  public void start(Future<Void> startFuture) throws Exception{

    Router projects = Router.router(vertx);
    Router customers = Router.router(vertx);
    Router cap_user = Router.router(vertx);
    Router time_entry = Router.router(vertx);

    projects.route("/dev/timetotrack*").handler(BodyHandler.create());
    projects.route("/assets/*").handler(StaticHandler.create("assets"));

    projectControl.getAll((RoutingContext) projects);
    projectControl.addOne((RoutingContext) projects);
    projectControl.getOne((RoutingContext) projects);
    projectControl.updateOne((RoutingContext) projects);
    projectControl.deleteProject((RoutingContext) projects);


    customerControl.getAll((RoutingContext) customers);
    customerControl.addOne((RoutingContext) customers);
    customerControl.getOne((RoutingContext) customers);
    customerControl.updateOne((RoutingContext) customers);
    customerControl.deleteCustomer((RoutingContext) customers);


    cap_userControl.getAll((RoutingContext) cap_user);
    cap_userControl.addOne((RoutingContext) cap_user);
    cap_userControl.getOne((RoutingContext) cap_user);
    cap_userControl.updateOne((RoutingContext) cap_user);
    cap_userControl.deleteCap_user((RoutingContext) cap_user);

    time_entryControl.getAll((RoutingContext) time_entry);
    time_entryControl.addOne((RoutingContext) time_entry);
    time_entryControl.getOne((RoutingContext) time_entry);
    time_entryControl.updateOne((RoutingContext) time_entry);
    time_entryControl.deleteTime_entry((RoutingContext) time_entry);


  }

}


