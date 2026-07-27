package com.pbec.preboardexamchecker.domain.usecase;

import android.content.Context;
import com.pbec.preboardexamchecker.domain.pdf.ClassReportPdfGenerator;
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
public final class ExportClassReportUseCase_Factory implements Factory<ExportClassReportUseCase> {
  private final Provider<Context> contextProvider;

  private final Provider<ClassReportPdfGenerator> generatorProvider;

  public ExportClassReportUseCase_Factory(Provider<Context> contextProvider,
      Provider<ClassReportPdfGenerator> generatorProvider) {
    this.contextProvider = contextProvider;
    this.generatorProvider = generatorProvider;
  }

  @Override
  public ExportClassReportUseCase get() {
    return newInstance(contextProvider.get(), generatorProvider.get());
  }

  public static ExportClassReportUseCase_Factory create(Provider<Context> contextProvider,
      Provider<ClassReportPdfGenerator> generatorProvider) {
    return new ExportClassReportUseCase_Factory(contextProvider, generatorProvider);
  }

  public static ExportClassReportUseCase newInstance(Context context,
      ClassReportPdfGenerator generator) {
    return new ExportClassReportUseCase(context, generator);
  }
}
