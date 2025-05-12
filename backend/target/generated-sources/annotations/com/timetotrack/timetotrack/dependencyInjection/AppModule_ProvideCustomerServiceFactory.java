package com.timetotrack.timetotrack.dependencyInjection;

import com.timetotrack.timetotrack.dao.CustomerDao;
import com.timetotrack.timetotrack.service.CustomerService;
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
public final class AppModule_ProvideCustomerServiceFactory implements Factory<CustomerService> {
  private final AppModule module;

  private final Provider<CustomerDao> customerDaoProvider;

  public AppModule_ProvideCustomerServiceFactory(AppModule module,
      Provider<CustomerDao> customerDaoProvider) {
    this.module = module;
    this.customerDaoProvider = customerDaoProvider;
  }

  @Override
  public CustomerService get() {
    return provideCustomerService(module, customerDaoProvider.get());
  }

  public static AppModule_ProvideCustomerServiceFactory create(AppModule module,
      Provider<CustomerDao> customerDaoProvider) {
    return new AppModule_ProvideCustomerServiceFactory(module, customerDaoProvider);
  }

  public static CustomerService provideCustomerService(AppModule instance,
      CustomerDao customerDao) {
    return Preconditions.checkNotNullFromProvides(instance.provideCustomerService(customerDao));
  }
}
