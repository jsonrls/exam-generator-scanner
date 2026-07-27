package com.pbec.preboardexamchecker;

import com.pbec.preboardexamchecker.data.repository.IExamRepository;
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
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<IExamRepository> examRepositoryInterfaceProvider;

  public MainActivity_MembersInjector(Provider<IExamRepository> examRepositoryInterfaceProvider) {
    this.examRepositoryInterfaceProvider = examRepositoryInterfaceProvider;
  }

  public static MembersInjector<MainActivity> create(
      Provider<IExamRepository> examRepositoryInterfaceProvider) {
    return new MainActivity_MembersInjector(examRepositoryInterfaceProvider);
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectExamRepositoryInterface(instance, examRepositoryInterfaceProvider.get());
  }

  @InjectedFieldSignature("com.pbec.preboardexamchecker.MainActivity.examRepositoryInterface")
  public static void injectExamRepositoryInterface(MainActivity instance,
      IExamRepository examRepositoryInterface) {
    instance.examRepositoryInterface = examRepositoryInterface;
  }
}
