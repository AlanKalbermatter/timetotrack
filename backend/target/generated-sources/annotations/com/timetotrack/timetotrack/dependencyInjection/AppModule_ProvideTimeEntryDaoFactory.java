package com.timetotrack.timetotrack.dependencyInjection;

import com.timetotrack.timetotrack.dao.TimeEntryDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.vertx.sqlclient.Pool;
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
public final class AppModule_ProvideTimeEntryDaoFactory implements Factory<TimeEntryDao> {
  private final AppModule module;

  private final Provider<Pool> poolProvider;

  public AppModule_ProvideTimeEntryDaoFactory(AppModule module, Provider<Pool> poolProvider) {
    this.module = module;
    this.poolProvider = poolProvider;
  }

  @Override
  public TimeEntryDao get() {
    return provideTimeEntryDao(module, poolProvider.get());
  }

  public static AppModule_ProvideTimeEntryDaoFactory create(AppModule module,
      Provider<Pool> poolProvider) {
    return new AppModule_ProvideTimeEntryDaoFactory(module, poolProvider);
  }

  public static TimeEntryDao provideTimeEntryDao(AppModule instance, Pool pool) {
    return Preconditions.checkNotNullFromProvides(instance.provideTimeEntryDao(pool));
  }
}
