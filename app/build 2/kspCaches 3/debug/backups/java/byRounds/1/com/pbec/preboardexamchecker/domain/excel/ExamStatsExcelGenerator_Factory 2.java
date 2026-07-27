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
public final class ExamStatsExcelGenerator_Factory implements Factory<ExamStatsExcelGenerator> {
  @Override
  public ExamStatsExcelGenerator get() {
    return newInstance();
  }

  public static ExamStatsExcelGenerator_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static ExamStatsExcelGenerator newInstance() {
    return new ExamStatsExcelGenerator();
  }

  private static final class InstanceHolder {
    private static final ExamStatsExcelGenerator_Factory INSTANCE = new ExamStatsExcelGenerator_Factory();
  }
}
