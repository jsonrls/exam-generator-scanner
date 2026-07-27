package com.pbec.preboardexamchecker.data.repository;

import android.content.Context;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pbec.preboardexamchecker.data.dao.ScanResultDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class ScanResultRepository_Factory implements Factory<ScanResultRepository> {
  private final Provider<ScanResultDao> daoProvider;

  private final Provider<FirebaseFirestore> firestoreProvider;

  private final Provider<IExamRepository> examRepositoryProvider;

  private final Provider<Context> contextProvider;

  public ScanResultRepository_Factory(Provider<ScanResultDao> daoProvider,
      Provider<FirebaseFirestore> firestoreProvider,
      Provider<IExamRepository> examRepositoryProvider, Provider<Context> contextProvider) {
    this.daoProvider = daoProvider;
    this.firestoreProvider = firestoreProvider;
    this.examRepositoryProvider = examRepositoryProvider;
    this.contextProvider = contextProvider;
  }

  @Override
  public ScanResultRepository get() {
    return newInstance(daoProvider.get(), firestoreProvider.get(), examRepositoryProvider.get(), contextProvider.get());
  }

  public static ScanResultRepository_Factory create(Provider<ScanResultDao> daoProvider,
      Provider<FirebaseFirestore> firestoreProvider,
      Provider<IExamRepository> examRepositoryProvider, Provider<Context> contextProvider) {
    return new ScanResultRepository_Factory(daoProvider, firestoreProvider, examRepositoryProvider, contextProvider);
  }

  public static ScanResultRepository newInstance(ScanResultDao dao, FirebaseFirestore firestore,
      IExamRepository examRepository, Context context) {
    return new ScanResultRepository(dao, firestore, examRepository, context);
  }
}
