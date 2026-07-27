package com.pbec.preboardexamchecker.di;

import com.pbec.preboardexamchecker.data.AppDatabase;
import com.pbec.preboardexamchecker.data.dao.ExamDao;
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
public final class DatabaseModule_ProvideExamDaoFactory implements Factory<ExamDao> {
  private final Provider<AppDatabase> appDatabaseProvider;

  public DatabaseModule_ProvideExamDaoFactory(Provider<AppDatabase> appDatabaseProvider) {
    this.appDatabaseProvider = appDatabaseProvider;
  }

  @Override
  public ExamDao get() {
    return provideExamDao(appDatabaseProvider.get());
  }

  public static DatabaseModule_ProvideExamDaoFactory create(
      Provider<AppDatabase> appDatabaseProvider) {
    return new DatabaseModule_ProvideExamDaoFactory(appDatabaseProvider);
  }

  public static ExamDao provideExamDao(AppDatabase appDatabase) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideExamDao(appDatabase));
  }
}
