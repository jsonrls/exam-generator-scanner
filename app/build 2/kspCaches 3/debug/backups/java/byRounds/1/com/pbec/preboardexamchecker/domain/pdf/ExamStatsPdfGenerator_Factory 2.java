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
public final class ExamStatsPdfGenerator_Factory implements Factory<ExamStatsPdfGenerator> {
  @Override
  public ExamStatsPdfGenerator get() {
    return newInstance();
  }

  public static ExamStatsPdfGenerator_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static ExamStatsPdfGenerator newInstance() {
    return new ExamStatsPdfGenerator();
  }

  private static final class InstanceHolder {
    private static final ExamStatsPdfGenerator_Factory INSTANCE = new ExamStatsPdfGenerator_Factory();
  }
}
