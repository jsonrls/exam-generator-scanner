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
public final class RosterTemplateGenerator_Factory implements Factory<RosterTemplateGenerator> {
  @Override
  public RosterTemplateGenerator get() {
    return newInstance();
  }

  public static RosterTemplateGenerator_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static RosterTemplateGenerator newInstance() {
    return new RosterTemplateGenerator();
  }

  private static final class InstanceHolder {
    private static final RosterTemplateGenerator_Factory INSTANCE = new RosterTemplateGenerator_Factory();
  }
}
