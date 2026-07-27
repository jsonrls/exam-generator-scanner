package com.pbec.preboardexamchecker.data.repository;

import com.google.firebase.firestore.FirebaseFirestore;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class ExamClusterRepository_Factory implements Factory<ExamClusterRepository> {
  private final Provider<FirebaseFirestore> firestoreProvider;

  public ExamClusterRepository_Factory(Provider<FirebaseFirestore> firestoreProvider) {
    this.firestoreProvider = firestoreProvider;
  }

  @Override
  public ExamClusterRepository get() {
    return newInstance(firestoreProvider.get());
  }

  public static ExamClusterRepository_Factory create(
      Provider<FirebaseFirestore> firestoreProvider) {
    return new ExamClusterRepository_Factory(firestoreProvider);
  }

  public static ExamClusterRepository newInstance(FirebaseFirestore firestore) {
    return new ExamClusterRepository(firestore);
  }
}
