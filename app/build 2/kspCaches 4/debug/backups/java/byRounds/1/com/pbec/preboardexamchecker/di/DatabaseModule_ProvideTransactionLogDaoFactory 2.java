package com.pbec.preboardexamchecker.di;

import com.pbec.preboardexamchecker.data.AppDatabase;
import com.pbec.preboardexamchecker.data.dao.TransactionLogDao;
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
public final class DatabaseModule_ProvideTransactionLogDaoFactory implements Factory<TransactionLogDao> {
  private final Provider<AppDatabase> appDatabaseProvider;

  public DatabaseModule_ProvideTransactionLogDaoFactory(Provider<AppDatabase> appDatabaseProvider) {
    this.appDatabaseProvider = appDatabaseProvider;
  }

  @Override
  public TransactionLogDao get() {
    return provideTransactionLogDao(appDatabaseProvider.get());
  }

  public static DatabaseModule_ProvideTransactionLogDaoFactory create(
      Provider<AppDatabase> appDatabaseProvider) {
    return new DatabaseModule_ProvideTransactionLogDaoFactory(appDatabaseProvider);
  }

  public static TransactionLogDao provideTransactionLogDao(AppDatabase appDatabase) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideTransactionLogDao(appDatabase));
  }
}
