package com.timetotrack.timetotrack.dependencyInjection;

import com.timetotrack.timetotrack.api.SwaggerVerticle;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
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
public final class AppModule_ProvideSwaggerVerticleFactory implements Factory<SwaggerVerticle> {
  private final AppModule module;

  public AppModule_ProvideSwaggerVerticleFactory(AppModule module) {
    this.module = module;
  }

  @Override
  public SwaggerVerticle get() {
    return provideSwaggerVerticle(module);
  }

  public static AppModule_ProvideSwaggerVerticleFactory create(AppModule module) {
    return new AppModule_ProvideSwaggerVerticleFactory(module);
  }

  public static SwaggerVerticle provideSwaggerVerticle(AppModule instance) {
    return Preconditions.checkNotNullFromProvides(instance.provideSwaggerVerticle());
  }
}
