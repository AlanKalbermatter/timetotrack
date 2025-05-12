package com.timetotrack.timetotrack.dependencyInjection;

import com.timetotrack.timetotrack.api.UserApiVerticle;
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
public final class AppModule_ProvideUserApiVerticleFactory implements Factory<UserApiVerticle> {
  private final AppModule module;

  private final Provider<UserService> userServiceProvider;

  public AppModule_ProvideUserApiVerticleFactory(AppModule module,
      Provider<UserService> userServiceProvider) {
    this.module = module;
    this.userServiceProvider = userServiceProvider;
  }

  @Override
  public UserApiVerticle get() {
    return provideUserApiVerticle(module, userServiceProvider.get());
  }

  public static AppModule_ProvideUserApiVerticleFactory create(AppModule module,
      Provider<UserService> userServiceProvider) {
    return new AppModule_ProvideUserApiVerticleFactory(module, userServiceProvider);
  }

  public static UserApiVerticle provideUserApiVerticle(AppModule instance,
      UserService userService) {
    return Preconditions.checkNotNullFromProvides(instance.provideUserApiVerticle(userService));
  }
}
