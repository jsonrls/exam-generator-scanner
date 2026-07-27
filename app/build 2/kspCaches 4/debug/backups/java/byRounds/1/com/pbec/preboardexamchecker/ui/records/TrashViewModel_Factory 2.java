package com.pbec.preboardexamchecker.ui.records;

import com.google.firebase.firestore.FirebaseFirestore;
import com.pbec.preboardexamchecker.data.repository.ExamRepository;
import com.pbec.preboardexamchecker.data.repository.IScanResultRepository;
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
public final class TrashViewModel_Factory implements Factory<TrashViewModel> {
  private final Provider<IScanResultRepository> repositoryProvider;

  private final Provider<ExamRepository> examRepositoryProvider;

  private final Provider<QuestionRepository> questionRepositoryProvider;

  private final Provider<FirebaseFirestore> firestoreProvider;

  public TrashViewModel_Factory(Provider<IScanResultRepository> repositoryProvider,
      Provider<ExamRepository> examRepositoryProvider,
      Provider<QuestionRepository> questionRepositoryProvider,
      Provider<FirebaseFirestore> firestoreProvider) {
    this.repositoryProvider = repositoryProvider;
    this.examRepositoryProvider = examRepositoryProvider;
    this.questionRepositoryProvider = questionRepositoryProvider;
    this.firestoreProvider = firestoreProvider;
  }

  @Override
  public TrashViewModel get() {
    return newInstance(repositoryProvider.get(), examRepositoryProvider.get(), questionRepositoryProvider.get(), firestoreProvider.get());
  }

  public static TrashViewModel_Factory create(Provider<IScanResultRepository> repositoryProvider,
      Provider<ExamRepository> examRepositoryProvider,
      Provider<QuestionRepository> questionRepositoryProvider,
      Provider<FirebaseFirestore> firestoreProvider) {
    return new TrashViewModel_Factory(repositoryProvider, examRepositoryProvider, questionRepositoryProvider, firestoreProvider);
  }

  public static TrashViewModel newInstance(IScanResultRepository repository,
      ExamRepository examRepository, QuestionRepository questionRepository,
      FirebaseFirestore firestore) {
    return new TrashViewModel(repository, examRepository, questionRepository, firestore);
  }
}
