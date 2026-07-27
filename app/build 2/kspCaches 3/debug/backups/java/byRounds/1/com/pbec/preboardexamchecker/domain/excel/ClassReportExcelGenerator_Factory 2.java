package com.pbec.preboardexamchecker.domain.excel;

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
public final class ClassReportExcelGenerator_Factory implements Factory<ClassReportExcelGenerator> {
  @Override
  public ClassReportExcelGenerator get() {
    return newInstance();
  }

  public static ClassReportExcelGenerator_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static ClassReportExcelGenerator newInstance() {
    return new ClassReportExcelGenerator();
  }

  private static final class InstanceHolder {
    private static final ClassReportExcelGenerator_Factory INSTANCE = new ClassReportExcelGenerator_Factory();
  }
}
