package com.pbec.preboardexamchecker.ui.scanner;

import android.content.Context;
import com.pbec.preboardexamchecker.data.repository.IExamClusterRepository;
import com.pbec.preboardexamchecker.data.repository.IExamRepository;
import com.pbec.preboardexamchecker.data.repository.IScanResultRepository;
import com.pbec.preboardexamchecker.data.repository.IStudentRepository;
import com.pbec.preboardexamchecker.data.repository.QuestionRepository;
import com.pbec.preboardexamchecker.ui.scanner.scoring.ScoringStrategy;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class ScannerViewModel_Factory implements Factory<ScannerViewModel> {
  private final Provider<IStudentRepository> studentRepositoryProvider;

  private final Provider<IExamRepository> examRepositoryProvider;

  private final Provider<IExamClusterRepository> clusterRepositoryProvider;

  private final Provider<IScanResultRepository> scanResultRepositoryProvider;

  private final Provider<ScoringStrategy> scoringStrategyProvider;

  private final Provider<QuestionRepository> questionRepositoryProvider;

  private final Provider<Context> appContextProvider;

  public ScannerViewModel_Factory(Provider<IStudentRepository> studentRepositoryProvider,
      Provider<IExamRepository> examRepositoryProvider,
      Provider<IExamClusterRepository> clusterRepositoryProvider,
      Provider<IScanResultRepository> scanResultRepositoryProvider,
      Provider<ScoringStrategy> scoringStrategyProvider,
      Provider<QuestionRepository> questionRepositoryProvider,
      Provider<Context> appContextProvider) {
    this.studentRepositoryProvider = studentRepositoryProvider;
    this.examRepositoryProvider = examRepositoryProvider;
    this.clusterRepositoryProvider = clusterRepositoryProvider;
    this.scanResultRepositoryProvider = scanResultRepositoryProvider;
    this.scoringStrategyProvider = scoringStrategyProvider;
    this.questionRepositoryProvider = questionRepositoryProvider;
    this.appContextProvider = appContextProvider;
  }

  @Override
  public ScannerViewModel get() {
    return newInstance(studentRepositoryProvider.get(), examRepositoryProvider.get(), clusterRepositoryProvider.get(), scanResultRepositoryProvider.get(), scoringStrategyProvider.get(), questionRepositoryProvider.get(), appContextProvider.get());
  }

  public static ScannerViewModel_Factory create(
      Provider<IStudentRepository> studentRepositoryProvider,
      Provider<IExamRepository> examRepositoryProvider,
      Provider<IExamClusterRepository> clusterRepositoryProvider,
      Provider<IScanResultRepository> scanResultRepositoryProvider,
      Provider<ScoringStrategy> scoringStrategyProvider,
      Provider<QuestionRepository> questionRepositoryProvider,
      Provider<Context> appContextProvider) {
    return new ScannerViewModel_Factory(studentRepositoryProvider, examRepositoryProvider, clusterRepositoryProvider, scanResultRepositoryProvider, scoringStrategyProvider, questionRepositoryProvider, appContextProvider);
  }

  public static ScannerViewModel newInstance(IStudentRepository studentRepository,
      IExamRepository examRepository, IExamClusterRepository clusterRepository,
      IScanResultRepository scanResultRepository, ScoringStrategy scoringStrategy,
      QuestionRepository questionRepository, Context appContext) {
    return new ScannerViewModel(studentRepository, examRepository, clusterRepository, scanResultRepository, scoringStrategy, questionRepository, appContext);
  }
}
