package com.timetotrack.timetotrack.dependencyInjection;

import com.timetotrack.timetotrack.api.CustomerApiVerticle;
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
public final class AppModule_ProvideCustomerApiVerticleFactory implements Factory<CustomerApiVerticle> {
  private final AppModule module;

  private final Provider<CustomerService> customerServiceProvider;

  public AppModule_ProvideCustomerApiVerticleFactory(AppModule module,
      Provider<CustomerService> customerServiceProvider) {
    this.module = module;
    this.customerServiceProvider = customerServiceProvider;
  }

  @Override
  public CustomerApiVerticle get() {
    return provideCustomerApiVerticle(module, customerServiceProvider.get());
  }

  public static AppModule_ProvideCustomerApiVerticleFactory create(AppModule module,
      Provider<CustomerService> customerServiceProvider) {
    return new AppModule_ProvideCustomerApiVerticleFactory(module, customerServiceProvider);
  }

  public static CustomerApiVerticle provideCustomerApiVerticle(AppModule instance,
      CustomerService customerService) {
    return Preconditions.checkNotNullFromProvides(instance.provideCustomerApiVerticle(customerService));
  }
}
