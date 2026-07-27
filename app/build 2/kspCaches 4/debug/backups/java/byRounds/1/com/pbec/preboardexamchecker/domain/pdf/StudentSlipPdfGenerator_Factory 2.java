package com.pbec.preboardexamchecker.domain.pdf;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class StudentSlipPdfGenerator_Factory implements Factory<StudentSlipPdfGenerator> {
  @Override
  public StudentSlipPdfGenerator get() {
    return newInstance();
  }

  public static StudentSlipPdfGenerator_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static StudentSlipPdfGenerator newInstance() {
    return new StudentSlipPdfGenerator();
  }

  private static final class InstanceHolder {
    private static final StudentSlipPdfGenerator_Factory INSTANCE = new StudentSlipPdfGenerator_Factory();
  }
}
