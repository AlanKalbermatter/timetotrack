package com.timetotrack.timetotrack.dependencyInjection;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.vertx.sqlclient.Pool;
import javax.annotation.processing.Generated;

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
public final class AppModule_ProvidePoolFactory implements Factory<Pool> {
  private final AppModule module;

  public AppModule_ProvidePoolFactory(AppModule module) {
    this.module = module;
  }

  @Override
  public Pool get() {
    return providePool(module);
  }

  public static AppModule_ProvidePoolFactory create(AppModule module) {
    return new AppModule_ProvidePoolFactory(module);
  }

  public static Pool providePool(AppModule instance) {
    return Preconditions.checkNotNullFromProvides(instance.providePool());
  }
}
