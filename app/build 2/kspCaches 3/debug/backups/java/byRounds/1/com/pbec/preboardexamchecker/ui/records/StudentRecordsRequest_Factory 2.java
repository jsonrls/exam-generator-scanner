package com.pbec.preboardexamchecker.ui.records;

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
public final class StudentRecordsRequest_Factory implements Factory<StudentRecordsRequest> {
  @Override
  public StudentRecordsRequest get() {
    return newInstance();
  }

  public static StudentRecordsRequest_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static StudentRecordsRequest newInstance() {
    return new StudentRecordsRequest();
  }

  private static final class InstanceHolder {
    private static final StudentRecordsRequest_Factory INSTANCE = new StudentRecordsRequest_Factory();
  }
}
