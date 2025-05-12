package com.timetotrack.timetotrack.dependencyInjection;

import com.timetotrack.timetotrack.api.*;
import com.timetotrack.timetotrack.dao.CustomerDao;
import com.timetotrack.timetotrack.dao.ProjectDao;
import com.timetotrack.timetotrack.dao.TimeEntryDao;
import com.timetotrack.timetotrack.dao.UserDao;
import com.timetotrack.timetotrack.service.CustomerService;
import com.timetotrack.timetotrack.service.ProjectService;
import com.timetotrack.timetotrack.service.TimeEntryService;
import com.timetotrack.timetotrack.service.UserService;
import dagger.Module;
import dagger.Provides;
import io.vertx.core.Vertx;
import io.vertx.sqlclient.Pool;

import javax.inject.Singleton;

@Module
public class AppModule {
    private final Vertx vertx;
    private final Pool pool;
    private final int userPort;
    private final int customerPort;
    private final int projectPort;
    private final int timeEntryPort;
    private final int swaggerPort;

    public AppModule(Vertx vertx, Pool pool, int userPort, int customerPort, int projectPort, int timeEntryPort, int swaggerPort) {
        this.vertx = vertx;
        this.pool = pool;
        this.userPort = userPort;
        this.customerPort = customerPort;
        this.projectPort = projectPort;
        this.timeEntryPort = timeEntryPort;
        this.swaggerPort = swaggerPort;
    }

    @Provides
    @Singleton
    Vertx provideVertx() {
        return vertx;
    }

    @Provides
    @Singleton
    Pool providePool() {
        return pool;
    }

    @Provides
    @Singleton
    UserDao provideUserDao(Pool pool) {
        return new UserDao(pool);
    }

    @Provides
    @Singleton
    UserService provideUserService(UserDao userDao) {
        return new UserService(userDao);
    }

    @Provides
    @Singleton
    UserApiVerticle provideUserApiVerticle(UserService userService) {
        return new UserApiVerticle(userService, userPort);
    }

    @Provides
    @Singleton
    CustomerDao provideCustomerDao(Pool pool) {
        return new CustomerDao(pool);
    }

    @Provides
    @Singleton
    CustomerService provideCustomerService(CustomerDao customerDao) {
        return new CustomerService(customerDao);
    }

    @Provides
    @Singleton
    CustomerApiVerticle provideCustomerApiVerticle(CustomerService customerService) {
        return new CustomerApiVerticle(customerService, customerPort);
    }

    @Provides
    @Singleton
    ProjectDao provideProjectDao(Pool pool) {
        return new ProjectDao(pool);
    }

    @Provides
    @Singleton
    ProjectService provideProjectService(ProjectDao projectDao) {
        return new ProjectService(projectDao);
    }

    @Provides
    @Singleton
    ProjectApiVerticle provideProjectApiVerticle(ProjectService projectService) {
        return new ProjectApiVerticle(projectService, projectPort);
    }

    @Provides
    @Singleton
    TimeEntryDao provideTimeEntryDao(Pool pool) {
        return new TimeEntryDao(pool);
    }

    @Provides
    @Singleton
    TimeEntryService provideTimeEntryService(TimeEntryDao timeEntryDao) {
        return new TimeEntryService(timeEntryDao);
    }

    @Provides
    @Singleton
    TimeEntryApiVerticle provideTimeEntryApiVerticle(TimeEntryService timeEntryService) {
        return new TimeEntryApiVerticle(timeEntryService, timeEntryPort);
    }

    @Provides
    @Singleton
    SwaggerVerticle provideSwaggerVerticle() {
        return new SwaggerVerticle(swaggerPort);
    }
}
