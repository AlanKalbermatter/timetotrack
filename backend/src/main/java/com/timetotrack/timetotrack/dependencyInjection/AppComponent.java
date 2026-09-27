package com.timetotrack.timetotrack.dependencyInjection;

import com.timetotrack.timetotrack.api.AuthApiVerticle;
import com.timetotrack.timetotrack.api.CustomerApiVerticle;
import com.timetotrack.timetotrack.api.DocsVerticle;
import com.timetotrack.timetotrack.api.GatewayVerticle;
import com.timetotrack.timetotrack.api.ProjectApiVerticle;
import com.timetotrack.timetotrack.api.TimeEntryApiVerticle;
import com.timetotrack.timetotrack.api.UserApiVerticle;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {

    AuthApiVerticle authApiVerticle();

    UserApiVerticle userApiVerticle();

    CustomerApiVerticle customerApiVerticle();

    ProjectApiVerticle projectApiVerticle();

    TimeEntryApiVerticle timeEntryApiVerticle();

    DocsVerticle docsVerticle();

    GatewayVerticle gatewayVerticle();
}
