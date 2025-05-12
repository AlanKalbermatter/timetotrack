package com.timetotrack.timetotrack.dependencyInjection;

import com.timetotrack.timetotrack.dao.UserDao;
import com.timetotrack.timetotrack.service.UserService;
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
public final class AppModule_ProvideUserServiceFactory implements Factory<UserService> {
  private final AppModule module;

  private final Provider<UserDao> userDaoProvider;

  public AppModule_ProvideUserServiceFactory(AppModule module, Provider<UserDao> userDaoProvider) {
    this.module = module;
    this.userDaoProvider = userDaoProvider;
  }

  @Override
  public UserService get() {
    return provideUserService(module, userDaoProvider.get());
  }

  public static AppModule_ProvideUserServiceFactory create(AppModule module,
      Provider<UserDao> userDaoProvider) {
    return new AppModule_ProvideUserServiceFactory(module, userDaoProvider);
  }

  public static UserService provideUserService(AppModule instance, UserDao userDao) {
    return Preconditions.checkNotNullFromProvides(instance.provideUserService(userDao));
  }
}
