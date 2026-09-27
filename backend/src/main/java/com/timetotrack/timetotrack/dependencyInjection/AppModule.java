package com.timetotrack.timetotrack.dependencyInjection;

import com.timetotrack.timetotrack.api.AuthApiVerticle;
import com.timetotrack.timetotrack.api.CustomerApiVerticle;
import com.timetotrack.timetotrack.api.DocsVerticle;
import com.timetotrack.timetotrack.api.GatewayVerticle;
import com.timetotrack.timetotrack.api.ProjectApiVerticle;
import com.timetotrack.timetotrack.api.TimeEntryApiVerticle;
import com.timetotrack.timetotrack.api.UserApiVerticle;
import com.timetotrack.timetotrack.auth.AuthService;
import com.timetotrack.timetotrack.auth.PasswordHasher;
import com.timetotrack.timetotrack.auth.TokenService;
import com.timetotrack.timetotrack.config.AppConfig;
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
import java.time.Clock;

@Module
public class AppModule {

    private final AppConfig config;
    private final Vertx vertx;
    private final Pool pool;

    public AppModule(AppConfig config, Vertx vertx, Pool pool) {
        this.config = config;
        this.vertx = vertx;
        this.pool = pool;
    }

    @Provides @Singleton Clock clock() { return Clock.systemUTC(); }

    @Provides @Singleton PasswordHasher passwordHasher() { return new PasswordHasher(); }

    @Provides @Singleton TokenService tokenService() { return new TokenService(vertx, config.jwtSecret()); }

    @Provides @Singleton UserDao userDao() { return new UserDao(pool); }

    @Provides @Singleton CustomerDao customerDao() { return new CustomerDao(pool); }

    @Provides @Singleton ProjectDao projectDao() { return new ProjectDao(pool); }

    @Provides @Singleton TimeEntryDao timeEntryDao() { return new TimeEntryDao(pool); }

    @Provides @Singleton
    AuthService authService(UserDao users, PasswordHasher hasher, TokenService tokens) {
        return new AuthService(vertx, users, hasher, tokens);
    }

    @Provides @Singleton UserService userService(UserDao users) { return new UserService(users); }

    @Provides @Singleton CustomerService customerService(CustomerDao customers) { return new CustomerService(customers); }

    @Provides @Singleton ProjectService projectService(ProjectDao projects) { return new ProjectService(projects); }

    @Provides @Singleton
    TimeEntryService timeEntryService(TimeEntryDao entries, Clock clock) {
        return new TimeEntryService(entries, clock);
    }

    @Provides AuthApiVerticle authApiVerticle(AuthService auth) {
        return new AuthApiVerticle(auth, config.ports().auth());
    }

    @Provides UserApiVerticle userApiVerticle(UserService users) {
        return new UserApiVerticle(users, config.ports().users());
    }

    @Provides CustomerApiVerticle customerApiVerticle(CustomerService customers) {
        return new CustomerApiVerticle(customers, config.ports().customers());
    }

    @Provides ProjectApiVerticle projectApiVerticle(ProjectService projects) {
        return new ProjectApiVerticle(projects, config.ports().projects());
    }

    @Provides TimeEntryApiVerticle timeEntryApiVerticle(TimeEntryService entries) {
        return new TimeEntryApiVerticle(entries, config.ports().timeEntries());
    }

    @Provides DocsVerticle docsVerticle() { return new DocsVerticle(config.ports().docs()); }

    @Provides GatewayVerticle gatewayVerticle(TokenService tokens) {
        return new GatewayVerticle(config.gatewayPort(), config.ports(), tokens);
    }
}
