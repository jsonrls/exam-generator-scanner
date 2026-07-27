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
public final class PreboardRecordExcelGenerator_Factory implements Factory<PreboardRecordExcelGenerator> {
  @Override
  public PreboardRecordExcelGenerator get() {
    return newInstance();
  }

  public static PreboardRecordExcelGenerator_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static PreboardRecordExcelGenerator newInstance() {
    return new PreboardRecordExcelGenerator();
  }

  private static final class InstanceHolder {
    private static final PreboardRecordExcelGenerator_Factory INSTANCE = new PreboardRecordExcelGenerator_Factory();
  }
}
