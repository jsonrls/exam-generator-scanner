package com.pbec.preboardexamchecker.ui.scanner.sync;

import android.content.Context;
import androidx.work.WorkerParameters;
import dagger.internal.DaggerGenerated;
import dagger.internal.InstanceFactory;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class ScanResultSyncWorker_AssistedFactory_Impl implements ScanResultSyncWorker_AssistedFactory {
  private final ScanResultSyncWorker_Factory delegateFactory;

  ScanResultSyncWorker_AssistedFactory_Impl(ScanResultSyncWorker_Factory delegateFactory) {
    this.delegateFactory = delegateFactory;
  }

  @Override
  public ScanResultSyncWorker create(Context p0, WorkerParameters p1) {
    return delegateFactory.get(p0, p1);
  }

  public static Provider<ScanResultSyncWorker_AssistedFactory> create(
      ScanResultSyncWorker_Factory delegateFactory) {
    return InstanceFactory.create(new ScanResultSyncWorker_AssistedFactory_Impl(delegateFactory));
  }

  public static dagger.internal.Provider<ScanResultSyncWorker_AssistedFactory> createFactoryProvider(
      ScanResultSyncWorker_Factory delegateFactory) {
    return InstanceFactory.create(new ScanResultSyncWorker_AssistedFactory_Impl(delegateFactory));
  }
}
