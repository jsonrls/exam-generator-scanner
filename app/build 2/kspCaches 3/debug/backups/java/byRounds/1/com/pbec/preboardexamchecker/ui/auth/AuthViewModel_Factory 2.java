package com.pbec.preboardexamchecker.ui.auth;

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
public final class AuthViewModel_Factory implements Factory<AuthViewModel> {
  private final Provider<FirebaseFirestore> firestoreProvider;

  private final Provider<Context> contextProvider;

  public AuthViewModel_Factory(Provider<FirebaseFirestore> firestoreProvider,
      Provider<Context> contextProvider) {
    this.firestoreProvider = firestoreProvider;
    this.contextProvider = contextProvider;
  }

  @Override
  public AuthViewModel get() {
    return newInstance(firestoreProvider.get(), contextProvider.get());
  }

  public static AuthViewModel_Factory create(Provider<FirebaseFirestore> firestoreProvider,
      Provider<Context> contextProvider) {
    return new AuthViewModel_Factory(firestoreProvider, contextProvider);
  }

  public static AuthViewModel newInstance(FirebaseFirestore firestore, Context context) {
    return new AuthViewModel(firestore, context);
  }
}
