package com.pbec.preboardexamchecker.di;

import com.pbec.preboardexamchecker.ui.scanner.scoring.ScoringStrategy;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class ScoringModule_ProvideScoringStrategyFactory implements Factory<ScoringStrategy> {
  @Override
  public ScoringStrategy get() {
    return provideScoringStrategy();
  }

  public static ScoringModule_ProvideScoringStrategyFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static ScoringStrategy provideScoringStrategy() {
    return Preconditions.checkNotNullFromProvides(ScoringModule.INSTANCE.provideScoringStrategy());
  }

  private static final class InstanceHolder {
    private static final ScoringModule_ProvideScoringStrategyFactory INSTANCE = new ScoringModule_ProvideScoringStrategyFactory();
  }
}
