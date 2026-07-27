package com.pbec.preboardexamchecker.ui.clusters;

import com.pbec.preboardexamchecker.data.repository.ExamClusterRepository;
import com.pbec.preboardexamchecker.data.repository.ExamRepository;
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
public final class ClusterViewModel_Factory implements Factory<ClusterViewModel> {
  private final Provider<ExamClusterRepository> clusterRepositoryProvider;

  private final Provider<ExamRepository> examRepositoryProvider;

  public ClusterViewModel_Factory(Provider<ExamClusterRepository> clusterRepositoryProvider,
      Provider<ExamRepository> examRepositoryProvider) {
    this.clusterRepositoryProvider = clusterRepositoryProvider;
    this.examRepositoryProvider = examRepositoryProvider;
  }

  @Override
  public ClusterViewModel get() {
    return newInstance(clusterRepositoryProvider.get(), examRepositoryProvider.get());
  }

  public static ClusterViewModel_Factory create(
      Provider<ExamClusterRepository> clusterRepositoryProvider,
      Provider<ExamRepository> examRepositoryProvider) {
    return new ClusterViewModel_Factory(clusterRepositoryProvider, examRepositoryProvider);
  }

  public static ClusterViewModel newInstance(ExamClusterRepository clusterRepository,
      ExamRepository examRepository) {
    return new ClusterViewModel(clusterRepository, examRepository);
  }
}
