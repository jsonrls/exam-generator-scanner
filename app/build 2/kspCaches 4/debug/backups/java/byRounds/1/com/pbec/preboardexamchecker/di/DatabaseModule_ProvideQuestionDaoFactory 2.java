package com.pbec.preboardexamchecker.di;

import com.pbec.preboardexamchecker.data.AppDatabase;
import com.pbec.preboardexamchecker.data.dao.QuestionDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideQuestionDaoFactory implements Factory<QuestionDao> {
  private final Provider<AppDatabase> appDatabaseProvider;

  public DatabaseModule_ProvideQuestionDaoFactory(Provider<AppDatabase> appDatabaseProvider) {
    this.appDatabaseProvider = appDatabaseProvider;
  }

  @Override
  public QuestionDao get() {
    return provideQuestionDao(appDatabaseProvider.get());
  }

  public static DatabaseModule_ProvideQuestionDaoFactory create(
      Provider<AppDatabase> appDatabaseProvider) {
    return new DatabaseModule_ProvideQuestionDaoFactory(appDatabaseProvider);
  }

  public static QuestionDao provideQuestionDao(AppDatabase appDatabase) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideQuestionDao(appDatabase));
  }
}
