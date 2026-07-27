package com.pbec.preboardexamchecker.ui.scanner.sync;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.pbec.preboardexamchecker.data.repository.IScanResultRepository;
import dagger.internal.DaggerGenerated;
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
public final class ScanResultSyncWorker_Factory {
  private final Provider<IScanResultRepository> repositoryProvider;

  public ScanResultSyncWorker_Factory(Provider<IScanResultRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  public ScanResultSyncWorker get(Context context, WorkerParameters params) {
    return newInstance(context, params, repositoryProvider.get());
  }

  public static ScanResultSyncWorker_Factory create(
      Provider<IScanResultRepository> repositoryProvider) {
    return new ScanResultSyncWorker_Factory(repositoryProvider);
  }

  public static ScanResultSyncWorker newInstance(Context context, WorkerParameters params,
      IScanResultRepository repository) {
    return new ScanResultSyncWorker(context, params, repository);
  }
}
