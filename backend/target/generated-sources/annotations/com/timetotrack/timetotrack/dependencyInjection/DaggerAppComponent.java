package com.timetotrack.timetotrack.dependencyInjection;

import com.timetotrack.timetotrack.api.CustomerApiVerticle;
import com.timetotrack.timetotrack.api.ProjectApiVerticle;
import com.timetotrack.timetotrack.api.SwaggerVerticle;
import com.timetotrack.timetotrack.api.TimeEntryApiVerticle;
import com.timetotrack.timetotrack.api.UserApiVerticle;
import com.timetotrack.timetotrack.dao.CustomerDao;
import com.timetotrack.timetotrack.dao.ProjectDao;
import com.timetotrack.timetotrack.dao.TimeEntryDao;
import com.timetotrack.timetotrack.dao.UserDao;
import com.timetotrack.timetotrack.service.CustomerService;
import com.timetotrack.timetotrack.service.ProjectService;
import com.timetotrack.timetotrack.service.TimeEntryService;
import com.timetotrack.timetotrack.service.UserService;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import io.vertx.sqlclient.Pool;
import javax.annotation.processing.Generated;

@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class DaggerAppComponent {
  private DaggerAppComponent() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private AppModule appModule;

    private Builder() {
    }

    public Builder appModule(AppModule appModule) {
      this.appModule = Preconditions.checkNotNull(appModule);
      return this;
    }

    public AppComponent build() {
      Preconditions.checkBuilderRequirement(appModule, AppModule.class);
      return new AppComponentImpl(appModule);
    }
  }

  private static final class AppComponentImpl implements AppComponent {
    private final AppComponentImpl appComponentImpl = this;

    private Provider<Pool> providePoolProvider;

    private Provider<UserDao> provideUserDaoProvider;

    private Provider<UserService> provideUserServiceProvider;

    private Provider<UserApiVerticle> provideUserApiVerticleProvider;

    private Provider<CustomerDao> provideCustomerDaoProvider;

    private Provider<CustomerService> provideCustomerServiceProvider;

    private Provider<CustomerApiVerticle> provideCustomerApiVerticleProvider;

    private Provider<ProjectDao> provideProjectDaoProvider;

    private Provider<ProjectService> provideProjectServiceProvider;

    private Provider<ProjectApiVerticle> provideProjectApiVerticleProvider;

    private Provider<TimeEntryDao> provideTimeEntryDaoProvider;

    private Provider<TimeEntryService> provideTimeEntryServiceProvider;

    private Provider<TimeEntryApiVerticle> provideTimeEntryApiVerticleProvider;

    private Provider<SwaggerVerticle> provideSwaggerVerticleProvider;

    private AppComponentImpl(AppModule appModuleParam) {

      initialize(appModuleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final AppModule appModuleParam) {
      this.providePoolProvider = DoubleCheck.provider(AppModule_ProvidePoolFactory.create(appModuleParam));
      this.provideUserDaoProvider = DoubleCheck.provider(AppModule_ProvideUserDaoFactory.create(appModuleParam, providePoolProvider));
      this.provideUserServiceProvider = DoubleCheck.provider(AppModule_ProvideUserServiceFactory.create(appModuleParam, provideUserDaoProvider));
      this.provideUserApiVerticleProvider = DoubleCheck.provider(AppModule_ProvideUserApiVerticleFactory.create(appModuleParam, provideUserServiceProvider));
      this.provideCustomerDaoProvider = DoubleCheck.provider(AppModule_ProvideCustomerDaoFactory.create(appModuleParam, providePoolProvider));
      this.provideCustomerServiceProvider = DoubleCheck.provider(AppModule_ProvideCustomerServiceFactory.create(appModuleParam, provideCustomerDaoProvider));
      this.provideCustomerApiVerticleProvider = DoubleCheck.provider(AppModule_ProvideCustomerApiVerticleFactory.create(appModuleParam, provideCustomerServiceProvider));
      this.provideProjectDaoProvider = DoubleCheck.provider(AppModule_ProvideProjectDaoFactory.create(appModuleParam, providePoolProvider));
      this.provideProjectServiceProvider = DoubleCheck.provider(AppModule_ProvideProjectServiceFactory.create(appModuleParam, provideProjectDaoProvider));
      this.provideProjectApiVerticleProvider = DoubleCheck.provider(AppModule_ProvideProjectApiVerticleFactory.create(appModuleParam, provideProjectServiceProvider));
      this.provideTimeEntryDaoProvider = DoubleCheck.provider(AppModule_ProvideTimeEntryDaoFactory.create(appModuleParam, providePoolProvider));
      this.provideTimeEntryServiceProvider = DoubleCheck.provider(AppModule_ProvideTimeEntryServiceFactory.create(appModuleParam, provideTimeEntryDaoProvider));
      this.provideTimeEntryApiVerticleProvider = DoubleCheck.provider(AppModule_ProvideTimeEntryApiVerticleFactory.create(appModuleParam, provideTimeEntryServiceProvider));
      this.provideSwaggerVerticleProvider = DoubleCheck.provider(AppModule_ProvideSwaggerVerticleFactory.create(appModuleParam));
    }

    @Override
    public UserApiVerticle userApiVerticle() {
      return provideUserApiVerticleProvider.get();
    }

    @Override
    public CustomerApiVerticle customerApiVerticle() {
      return provideCustomerApiVerticleProvider.get();
    }

    @Override
    public ProjectApiVerticle projectApiVerticle() {
      return provideProjectApiVerticleProvider.get();
    }

    @Override
    public TimeEntryApiVerticle timeEntryApiVerticle() {
      return provideTimeEntryApiVerticleProvider.get();
    }

    @Override
    public SwaggerVerticle swaggerVerticle() {
      return provideSwaggerVerticleProvider.get();
    }
  }
}
