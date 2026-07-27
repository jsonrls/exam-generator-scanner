package com.pbec.preboardexamchecker.di;

import com.pbec.preboardexamchecker.data.AppDatabase;
import com.pbec.preboardexamchecker.data.dao.ScanResultDao;
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
public final class DatabaseModule_ProvideScanResultDaoFactory implements Factory<ScanResultDao> {
  private final Provider<AppDatabase> appDatabaseProvider;

  public DatabaseModule_ProvideScanResultDaoFactory(Provider<AppDatabase> appDatabaseProvider) {
    this.appDatabaseProvider = appDatabaseProvider;
  }

  @Override
  public ScanResultDao get() {
    return provideScanResultDao(appDatabaseProvider.get());
  }

  public static DatabaseModule_ProvideScanResultDaoFactory create(
      Provider<AppDatabase> appDatabaseProvider) {
    return new DatabaseModule_ProvideScanResultDaoFactory(appDatabaseProvider);
  }

  public static ScanResultDao provideScanResultDao(AppDatabase appDatabase) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideScanResultDao(appDatabase));
  }
}
