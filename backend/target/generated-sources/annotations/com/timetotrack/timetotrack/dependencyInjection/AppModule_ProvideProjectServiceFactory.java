package com.timetotrack.timetotrack.dependencyInjection;

import com.timetotrack.timetotrack.dao.ProjectDao;
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
public final class AppModule_ProvideProjectServiceFactory implements Factory<ProjectService> {
  private final AppModule module;

  private final Provider<ProjectDao> projectDaoProvider;

  public AppModule_ProvideProjectServiceFactory(AppModule module,
      Provider<ProjectDao> projectDaoProvider) {
    this.module = module;
    this.projectDaoProvider = projectDaoProvider;
  }

  @Override
  public ProjectService get() {
    return provideProjectService(module, projectDaoProvider.get());
  }

  public static AppModule_ProvideProjectServiceFactory create(AppModule module,
      Provider<ProjectDao> projectDaoProvider) {
    return new AppModule_ProvideProjectServiceFactory(module, projectDaoProvider);
  }

  public static ProjectService provideProjectService(AppModule instance, ProjectDao projectDao) {
    return Preconditions.checkNotNullFromProvides(instance.provideProjectService(projectDao));
  }
}
