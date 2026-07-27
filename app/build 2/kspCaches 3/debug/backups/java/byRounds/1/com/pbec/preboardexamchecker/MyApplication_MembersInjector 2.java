package com.pbec.preboardexamchecker;

import androidx.hilt.work.HiltWorkerFactory;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class MyApplication_MembersInjector implements MembersInjector<MyApplication> {
  private final Provider<HiltWorkerFactory> workerFactoryProvider;

  public MyApplication_MembersInjector(Provider<HiltWorkerFactory> workerFactoryProvider) {
    this.workerFactoryProvider = workerFactoryProvider;
  }

  public static MembersInjector<MyApplication> create(
      Provider<HiltWorkerFactory> workerFactoryProvider) {
    return new MyApplication_MembersInjector(workerFactoryProvider);
  }

  @Override
  public void injectMembers(MyApplication instance) {
    injectWorkerFactory(instance, workerFactoryProvider.get());
  }

  @InjectedFieldSignature("com.pbec.preboardexamchecker.MyApplication.workerFactory")
  public static void injectWorkerFactory(MyApplication instance, HiltWorkerFactory workerFactory) {
    instance.workerFactory = workerFactory;
  }
}
