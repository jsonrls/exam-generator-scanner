package com.pbec.preboardexamchecker.ui.viewmodels;

import android.content.Context;
import androidx.lifecycle.SavedStateHandle;
import com.pbec.preboardexamchecker.data.repository.ExamRepository;
import com.pbec.preboardexamchecker.data.repository.QuestionRepository;
import com.pbec.preboardexamchecker.utils.PdfExportUtil;
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
public final class ExamContentViewModel_Factory implements Factory<ExamContentViewModel> {
  private final Provider<ExamRepository> examRepositoryProvider;

  private final Provider<QuestionRepository> questionRepositoryProvider;

  private final Provider<PdfExportUtil> pdfExportUtilProvider;

  private final Provider<Context> applicationContextProvider;

  private final Provider<SavedStateHandle> savedStateHandleProvider;

  public ExamContentViewModel_Factory(Provider<ExamRepository> examRepositoryProvider,
      Provider<QuestionRepository> questionRepositoryProvider,
      Provider<PdfExportUtil> pdfExportUtilProvider, Provider<Context> applicationContextProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    this.examRepositoryProvider = examRepositoryProvider;
    this.questionRepositoryProvider = questionRepositoryProvider;
    this.pdfExportUtilProvider = pdfExportUtilProvider;
    this.applicationContextProvider = applicationContextProvider;
    this.savedStateHandleProvider = savedStateHandleProvider;
  }

  @Override
  public ExamContentViewModel get() {
    return newInstance(examRepositoryProvider.get(), questionRepositoryProvider.get(), pdfExportUtilProvider.get(), applicationContextProvider.get(), savedStateHandleProvider.get());
  }

  public static ExamContentViewModel_Factory create(Provider<ExamRepository> examRepositoryProvider,
      Provider<QuestionRepository> questionRepositoryProvider,
      Provider<PdfExportUtil> pdfExportUtilProvider, Provider<Context> applicationContextProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    return new ExamContentViewModel_Factory(examRepositoryProvider, questionRepositoryProvider, pdfExportUtilProvider, applicationContextProvider, savedStateHandleProvider);
  }

  public static ExamContentViewModel newInstance(ExamRepository examRepository,
      QuestionRepository questionRepository, PdfExportUtil pdfExportUtil,
      Context applicationContext, SavedStateHandle savedStateHandle) {
    return new ExamContentViewModel(examRepository, questionRepository, pdfExportUtil, applicationContext, savedStateHandle);
  }
}
