package com.timetotrack.timetotrack.dependencyInjection;

import com.timetotrack.timetotrack.api.*;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    UserApiVerticle userApiVerticle();
    CustomerApiVerticle customerApiVerticle();
    ProjectApiVerticle projectApiVerticle();
    TimeEntryApiVerticle timeEntryApiVerticle();
    SwaggerVerticle swaggerVerticle();
}
