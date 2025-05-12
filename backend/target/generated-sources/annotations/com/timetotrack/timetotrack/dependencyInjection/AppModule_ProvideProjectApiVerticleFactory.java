package com.timetotrack.timetotrack.dependencyInjection;

import com.timetotrack.timetotrack.api.ProjectApiVerticle;
import com.timetotrack.timetotrack.service.ProjectService;
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
public final class AppModule_ProvideProjectApiVerticleFactory implements Factory<ProjectApiVerticle> {
  private final AppModule module;

  private final Provider<ProjectService> projectServiceProvider;

  public AppModule_ProvideProjectApiVerticleFactory(AppModule module,
      Provider<ProjectService> projectServiceProvider) {
    this.module = module;
    this.projectServiceProvider = projectServiceProvider;
  }

  @Override
  public ProjectApiVerticle get() {
    return provideProjectApiVerticle(module, projectServiceProvider.get());
  }

  public static AppModule_ProvideProjectApiVerticleFactory create(AppModule module,
      Provider<ProjectService> projectServiceProvider) {
    return new AppModule_ProvideProjectApiVerticleFactory(module, projectServiceProvider);
  }

  public static ProjectApiVerticle provideProjectApiVerticle(AppModule instance,
      ProjectService projectService) {
    return Preconditions.checkNotNullFromProvides(instance.provideProjectApiVerticle(projectService));
  }
}
