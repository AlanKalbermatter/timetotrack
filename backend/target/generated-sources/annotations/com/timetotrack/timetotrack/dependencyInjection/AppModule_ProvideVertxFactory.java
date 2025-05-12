package com.timetotrack.timetotrack.dependencyInjection;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.vertx.core.Vertx;
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
public final class AppModule_ProvideVertxFactory implements Factory<Vertx> {
  private final AppModule module;

  public AppModule_ProvideVertxFactory(AppModule module) {
    this.module = module;
  }

  @Override
  public Vertx get() {
    return provideVertx(module);
  }

  public static AppModule_ProvideVertxFactory create(AppModule module) {
    return new AppModule_ProvideVertxFactory(module);
  }

  public static Vertx provideVertx(AppModule instance) {
    return Preconditions.checkNotNullFromProvides(instance.provideVertx());
  }
}
