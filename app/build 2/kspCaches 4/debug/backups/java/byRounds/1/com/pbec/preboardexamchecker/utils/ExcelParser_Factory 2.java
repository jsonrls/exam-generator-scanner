package com.pbec.preboardexamchecker.utils;

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
public final class ExcelParser_Factory implements Factory<ExcelParser> {
  @Override
  public ExcelParser get() {
    return newInstance();
  }

  public static ExcelParser_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static ExcelParser newInstance() {
    return new ExcelParser();
  }

  private static final class InstanceHolder {
    private static final ExcelParser_Factory INSTANCE = new ExcelParser_Factory();
  }
}
