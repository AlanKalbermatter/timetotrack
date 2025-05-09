package com.timetotrack.timetotrack.di;

import com.timetotrack.timetotrack.api.CustomerApiVerticle;
import com.timetotrack.timetotrack.api.ProjectApiVerticle;
import com.timetotrack.timetotrack.api.UserApiVerticle;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    UserApiVerticle userApiVerticle();
    CustomerApiVerticle customerApiVerticle();
    ProjectApiVerticle projectApiVerticle();
}
