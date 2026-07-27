package com.pbec.preboardexamchecker.ui.account;

import android.content.Context;
import com.google.firebase.firestore.FirebaseFirestore;
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
public final class SecurityViewModel_Factory implements Factory<SecurityViewModel> {
  private final Provider<FirebaseFirestore> firestoreProvider;

  private final Provider<Context> contextProvider;

  public SecurityViewModel_Factory(Provider<FirebaseFirestore> firestoreProvider,
      Provider<Context> contextProvider) {
    this.firestoreProvider = firestoreProvider;
    this.contextProvider = contextProvider;
  }

  @Override
  public SecurityViewModel get() {
    return newInstance(firestoreProvider.get(), contextProvider.get());
  }

  public static SecurityViewModel_Factory create(Provider<FirebaseFirestore> firestoreProvider,
      Provider<Context> contextProvider) {
    return new SecurityViewModel_Factory(firestoreProvider, contextProvider);
  }

  public static SecurityViewModel newInstance(FirebaseFirestore firestore, Context context) {
    return new SecurityViewModel(firestore, context);
  }
}
