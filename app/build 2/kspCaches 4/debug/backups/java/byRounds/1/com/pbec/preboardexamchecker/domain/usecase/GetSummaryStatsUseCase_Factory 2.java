package com.pbec.preboardexamchecker.domain.usecase;

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
public final class GetSummaryStatsUseCase_Factory implements Factory<GetSummaryStatsUseCase> {
  @Override
  public GetSummaryStatsUseCase get() {
    return newInstance();
  }

  public static GetSummaryStatsUseCase_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static GetSummaryStatsUseCase newInstance() {
    return new GetSummaryStatsUseCase();
  }

  private static final class InstanceHolder {
    private static final GetSummaryStatsUseCase_Factory INSTANCE = new GetSummaryStatsUseCase_Factory();
  }
}
