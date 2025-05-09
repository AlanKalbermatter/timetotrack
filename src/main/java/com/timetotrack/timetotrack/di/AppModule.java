package com.timetotrack.timetotrack.di;

import com.timetotrack.timetotrack.api.CustomerApiVerticle;
import com.timetotrack.timetotrack.api.ProjectApiVerticle;
import com.timetotrack.timetotrack.dao.CustomerDao;
import com.timetotrack.timetotrack.dao.ProjectDao;
import com.timetotrack.timetotrack.dao.UserDao;
import com.timetotrack.timetotrack.service.CustomerService;
import com.timetotrack.timetotrack.service.ProjectService;
import com.timetotrack.timetotrack.service.UserService;
import com.timetotrack.timetotrack.api.UserApiVerticle;
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

    public AppModule(Vertx vertx, Pool pool, int userPort, int customerPort, int projectPort) {
        this.vertx = vertx;
        this.pool = pool;
        this.userPort = userPort;
        this.customerPort = customerPort;
        this.projectPort = projectPort;
    }

    @Provides @Singleton
    Vertx provideVertx() {
        return vertx;
    }

    @Provides @Singleton
    Pool providePool() {
        return pool;
    }

    @Provides @Singleton
    UserDao provideUserDao(Pool pool) {
        return new UserDao(pool);
    }

    @Provides @Singleton
    UserService provideUserService(UserDao userDao) {
        return new UserService(userDao);
    }

    @Provides @Singleton
    UserApiVerticle provideUserApiVerticle(UserService userService) {
        return new UserApiVerticle(userService, userPort);
    }

    @Provides @Singleton
    CustomerDao provideCustomerDao(Pool pool) {
        return new CustomerDao(pool);
    }

    @Provides @Singleton
    CustomerService provideCustomerService(CustomerDao customerDao) {
        return new CustomerService(customerDao);
    }

    @Provides @Singleton
    CustomerApiVerticle provideCustomerApiVerticle(CustomerService customerService) {
        return new CustomerApiVerticle(customerService, customerPort);
    }

    @Provides @Singleton
    ProjectDao provideProjectDao(Pool pool) {
        return new ProjectDao(pool);
    }

    @Provides @Singleton
    ProjectService provideProjectService(ProjectDao projectDao) {
        return new ProjectService(projectDao);
    }

    @Provides @Singleton
    ProjectApiVerticle provideProjectApiVerticle(ProjectService projectService) {
        return new ProjectApiVerticle(projectService, projectPort);
    }
}
