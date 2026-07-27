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
public final class ClassReportPdfGenerator_Factory implements Factory<ClassReportPdfGenerator> {
  @Override
  public ClassReportPdfGenerator get() {
    return newInstance();
  }

  public static ClassReportPdfGenerator_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static ClassReportPdfGenerator newInstance() {
    return new ClassReportPdfGenerator();
  }

  private static final class InstanceHolder {
    private static final ClassReportPdfGenerator_Factory INSTANCE = new ClassReportPdfGenerator_Factory();
  }
}
