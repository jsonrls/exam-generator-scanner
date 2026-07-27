package com.pbec.preboardexamchecker.domain.usecase;

import com.pbec.preboardexamchecker.data.repository.IExamRepository;
import com.pbec.preboardexamchecker.data.repository.QuestionRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class ComputeExamStatsUseCase_Factory implements Factory<ComputeExamStatsUseCase> {
  private final Provider<IExamRepository> examRepositoryProvider;

  private final Provider<QuestionRepository> questionRepositoryProvider;

  public ComputeExamStatsUseCase_Factory(Provider<IExamRepository> examRepositoryProvider,
      Provider<QuestionRepository> questionRepositoryProvider) {
    this.examRepositoryProvider = examRepositoryProvider;
    this.questionRepositoryProvider = questionRepositoryProvider;
  }

  @Override
  public ComputeExamStatsUseCase get() {
    return newInstance(examRepositoryProvider.get(), questionRepositoryProvider.get());
  }

  public static ComputeExamStatsUseCase_Factory create(
      Provider<IExamRepository> examRepositoryProvider,
      Provider<QuestionRepository> questionRepositoryProvider) {
    return new ComputeExamStatsUseCase_Factory(examRepositoryProvider, questionRepositoryProvider);
  }

  public static ComputeExamStatsUseCase newInstance(IExamRepository examRepository,
      QuestionRepository questionRepository) {
    return new ComputeExamStatsUseCase(examRepository, questionRepository);
  }
}
