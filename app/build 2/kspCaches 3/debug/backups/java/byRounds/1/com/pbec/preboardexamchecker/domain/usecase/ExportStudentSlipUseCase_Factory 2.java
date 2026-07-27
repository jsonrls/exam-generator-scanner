package com.pbec.preboardexamchecker.domain.usecase;

import android.content.Context;
import com.pbec.preboardexamchecker.domain.pdf.StudentSlipPdfGenerator;
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
public final class ExportStudentSlipUseCase_Factory implements Factory<ExportStudentSlipUseCase> {
  private final Provider<Context> contextProvider;

  private final Provider<StudentSlipPdfGenerator> generatorProvider;

  public ExportStudentSlipUseCase_Factory(Provider<Context> contextProvider,
      Provider<StudentSlipPdfGenerator> generatorProvider) {
    this.contextProvider = contextProvider;
    this.generatorProvider = generatorProvider;
  }

  @Override
  public ExportStudentSlipUseCase get() {
    return newInstance(contextProvider.get(), generatorProvider.get());
  }

  public static ExportStudentSlipUseCase_Factory create(Provider<Context> contextProvider,
      Provider<StudentSlipPdfGenerator> generatorProvider) {
    return new ExportStudentSlipUseCase_Factory(contextProvider, generatorProvider);
  }

  public static ExportStudentSlipUseCase newInstance(Context context,
      StudentSlipPdfGenerator generator) {
    return new ExportStudentSlipUseCase(context, generator);
  }
}
