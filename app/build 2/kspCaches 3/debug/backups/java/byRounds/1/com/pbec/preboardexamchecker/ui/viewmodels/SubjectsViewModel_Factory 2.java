package com.pbec.preboardexamchecker.ui.viewmodels;

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
public final class SubjectsViewModel_Factory implements Factory<SubjectsViewModel> {
  @Override
  public SubjectsViewModel get() {
    return newInstance();
  }

  public static SubjectsViewModel_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static SubjectsViewModel newInstance() {
    return new SubjectsViewModel();
  }

  private static final class InstanceHolder {
    private static final SubjectsViewModel_Factory INSTANCE = new SubjectsViewModel_Factory();
  }
}
