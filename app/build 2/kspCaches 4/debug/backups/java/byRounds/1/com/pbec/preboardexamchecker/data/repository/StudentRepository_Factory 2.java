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
public final class StudentRepository_Factory implements Factory<StudentRepository> {
  private final Provider<FirebaseFirestore> firestoreProvider;

  public StudentRepository_Factory(Provider<FirebaseFirestore> firestoreProvider) {
    this.firestoreProvider = firestoreProvider;
  }

  @Override
  public StudentRepository get() {
    return newInstance(firestoreProvider.get());
  }

  public static StudentRepository_Factory create(Provider<FirebaseFirestore> firestoreProvider) {
    return new StudentRepository_Factory(firestoreProvider);
  }

  public static StudentRepository newInstance(FirebaseFirestore firestore) {
    return new StudentRepository(firestore);
  }
}
