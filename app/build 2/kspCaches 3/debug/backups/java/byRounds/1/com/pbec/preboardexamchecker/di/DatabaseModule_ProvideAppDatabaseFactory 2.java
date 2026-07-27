package com.pbec.preboardexamchecker.di;

import android.content.Context;
import com.google.gson.Gson;
import com.pbec.preboardexamchecker.data.AppDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideAppDatabaseFactory implements Factory<AppDatabase> {
  private final Provider<Context> contextProvider;

  private final Provider<Gson> gsonProvider;

  public DatabaseModule_ProvideAppDatabaseFactory(Provider<Context> contextProvider,
      Provider<Gson> gsonProvider) {
    this.contextProvider = contextProvider;
    this.gsonProvider = gsonProvider;
  }

  @Override
  public AppDatabase get() {
    return provideAppDatabase(contextProvider.get(), gsonProvider.get());
  }

  public static DatabaseModule_ProvideAppDatabaseFactory create(Provider<Context> contextProvider,
      Provider<Gson> gsonProvider) {
    return new DatabaseModule_ProvideAppDatabaseFactory(contextProvider, gsonProvider);
  }

  public static AppDatabase provideAppDatabase(Context context, Gson gson) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideAppDatabase(context, gson));
  }
}
