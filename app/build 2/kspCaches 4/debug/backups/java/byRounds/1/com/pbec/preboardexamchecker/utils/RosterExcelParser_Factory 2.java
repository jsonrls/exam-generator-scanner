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
public final class RosterExcelParser_Factory implements Factory<RosterExcelParser> {
  @Override
  public RosterExcelParser get() {
    return newInstance();
  }

  public static RosterExcelParser_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static RosterExcelParser newInstance() {
    return new RosterExcelParser();
  }

  private static final class InstanceHolder {
    private static final RosterExcelParser_Factory INSTANCE = new RosterExcelParser_Factory();
  }
}
