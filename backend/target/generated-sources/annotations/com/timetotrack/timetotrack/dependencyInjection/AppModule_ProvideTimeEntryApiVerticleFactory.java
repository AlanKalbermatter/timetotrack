package com.timetotrack.timetotrack.dependencyInjection;

import com.timetotrack.timetotrack.api.TimeEntryApiVerticle;
import com.timetotrack.timetotrack.service.TimeEntryService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
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
public final class AppModule_ProvideTimeEntryApiVerticleFactory implements Factory<TimeEntryApiVerticle> {
  private final AppModule module;

  private final Provider<TimeEntryService> timeEntryServiceProvider;

  public AppModule_ProvideTimeEntryApiVerticleFactory(AppModule module,
      Provider<TimeEntryService> timeEntryServiceProvider) {
    this.module = module;
    this.timeEntryServiceProvider = timeEntryServiceProvider;
  }

  @Override
  public TimeEntryApiVerticle get() {
    return provideTimeEntryApiVerticle(module, timeEntryServiceProvider.get());
  }

  public static AppModule_ProvideTimeEntryApiVerticleFactory create(AppModule module,
      Provider<TimeEntryService> timeEntryServiceProvider) {
    return new AppModule_ProvideTimeEntryApiVerticleFactory(module, timeEntryServiceProvider);
  }

  public static TimeEntryApiVerticle provideTimeEntryApiVerticle(AppModule instance,
      TimeEntryService timeEntryService) {
    return Preconditions.checkNotNullFromProvides(instance.provideTimeEntryApiVerticle(timeEntryService));
  }
}
