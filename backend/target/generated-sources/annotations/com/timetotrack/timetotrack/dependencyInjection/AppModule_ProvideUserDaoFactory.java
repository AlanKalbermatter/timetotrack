package com.timetotrack.timetotrack.dependencyInjection;

import com.timetotrack.timetotrack.dao.UserDao;
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
public final class AppModule_ProvideUserDaoFactory implements Factory<UserDao> {
  private final AppModule module;

  private final Provider<Pool> poolProvider;

  public AppModule_ProvideUserDaoFactory(AppModule module, Provider<Pool> poolProvider) {
    this.module = module;
    this.poolProvider = poolProvider;
  }

  @Override
  public UserDao get() {
    return provideUserDao(module, poolProvider.get());
  }

  public static AppModule_ProvideUserDaoFactory create(AppModule module,
      Provider<Pool> poolProvider) {
    return new AppModule_ProvideUserDaoFactory(module, poolProvider);
  }

  public static UserDao provideUserDao(AppModule instance, Pool pool) {
    return Preconditions.checkNotNullFromProvides(instance.provideUserDao(pool));
  }
}
