package com.timetotrack.timetotrack.dependencyInjection;

import com.timetotrack.timetotrack.dao.TimeEntryDao;
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
public final class AppModule_ProvideTimeEntryServiceFactory implements Factory<TimeEntryService> {
  private final AppModule module;

  private final Provider<TimeEntryDao> timeEntryDaoProvider;

  public AppModule_ProvideTimeEntryServiceFactory(AppModule module,
      Provider<TimeEntryDao> timeEntryDaoProvider) {
    this.module = module;
    this.timeEntryDaoProvider = timeEntryDaoProvider;
  }

  @Override
  public TimeEntryService get() {
    return provideTimeEntryService(module, timeEntryDaoProvider.get());
  }

  public static AppModule_ProvideTimeEntryServiceFactory create(AppModule module,
      Provider<TimeEntryDao> timeEntryDaoProvider) {
    return new AppModule_ProvideTimeEntryServiceFactory(module, timeEntryDaoProvider);
  }

  public static TimeEntryService provideTimeEntryService(AppModule instance,
      TimeEntryDao timeEntryDao) {
    return Preconditions.checkNotNullFromProvides(instance.provideTimeEntryService(timeEntryDao));
  }
}
