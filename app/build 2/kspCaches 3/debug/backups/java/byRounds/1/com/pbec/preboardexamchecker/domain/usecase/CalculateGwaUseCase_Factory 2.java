package com.pbec.preboardexamchecker.domain.usecase;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
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
public final class CalculateGwaUseCase_Factory implements Factory<CalculateGwaUseCase> {
  @Override
  public CalculateGwaUseCase get() {
    return newInstance();
  }

  public static CalculateGwaUseCase_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static CalculateGwaUseCase newInstance() {
    return new CalculateGwaUseCase();
  }

  private static final class InstanceHolder {
    private static final CalculateGwaUseCase_Factory INSTANCE = new CalculateGwaUseCase_Factory();
  }
}
