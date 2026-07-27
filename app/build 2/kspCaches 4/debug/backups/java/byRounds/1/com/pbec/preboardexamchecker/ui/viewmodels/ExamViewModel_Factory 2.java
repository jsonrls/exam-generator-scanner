package com.pbec.preboardexamchecker.ui.viewmodels;

import android.content.Context;
import androidx.lifecycle.SavedStateHandle;
import com.pbec.preboardexamchecker.data.repository.ExamRepository;
import com.pbec.preboardexamchecker.data.repository.IScanResultRepository;
import com.pbec.preboardexamchecker.data.repository.QuestionRepository;
import com.pbec.preboardexamchecker.utils.ExcelParser;
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
public final class ExamViewModel_Factory implements Factory<ExamViewModel> {
  private final Provider<ExamRepository> examRepositoryProvider;

  private final Provider<QuestionRepository> questionRepositoryProvider;

  private final Provider<IScanResultRepository> scanResultRepositoryProvider;

  private final Provider<ExcelParser> excelParserProvider;

  private final Provider<PdfExportUtil> pdfExportUtilProvider;

  private final Provider<Context> contextProvider;

  private final Provider<SavedStateHandle> savedStateHandleProvider;

  public ExamViewModel_Factory(Provider<ExamRepository> examRepositoryProvider,
      Provider<QuestionRepository> questionRepositoryProvider,
      Provider<IScanResultRepository> scanResultRepositoryProvider,
      Provider<ExcelParser> excelParserProvider, Provider<PdfExportUtil> pdfExportUtilProvider,
      Provider<Context> contextProvider, Provider<SavedStateHandle> savedStateHandleProvider) {
    this.examRepositoryProvider = examRepositoryProvider;
    this.questionRepositoryProvider = questionRepositoryProvider;
    this.scanResultRepositoryProvider = scanResultRepositoryProvider;
    this.excelParserProvider = excelParserProvider;
    this.pdfExportUtilProvider = pdfExportUtilProvider;
    this.contextProvider = contextProvider;
    this.savedStateHandleProvider = savedStateHandleProvider;
  }

  @Override
  public ExamViewModel get() {
    return newInstance(examRepositoryProvider.get(), questionRepositoryProvider.get(), scanResultRepositoryProvider.get(), excelParserProvider.get(), pdfExportUtilProvider.get(), contextProvider.get(), savedStateHandleProvider.get());
  }

  public static ExamViewModel_Factory create(Provider<ExamRepository> examRepositoryProvider,
      Provider<QuestionRepository> questionRepositoryProvider,
      Provider<IScanResultRepository> scanResultRepositoryProvider,
      Provider<ExcelParser> excelParserProvider, Provider<PdfExportUtil> pdfExportUtilProvider,
      Provider<Context> contextProvider, Provider<SavedStateHandle> savedStateHandleProvider) {
    return new ExamViewModel_Factory(examRepositoryProvider, questionRepositoryProvider, scanResultRepositoryProvider, excelParserProvider, pdfExportUtilProvider, contextProvider, savedStateHandleProvider);
  }

  public static ExamViewModel newInstance(ExamRepository examRepository,
      QuestionRepository questionRepository, IScanResultRepository scanResultRepository,
      ExcelParser excelParser, PdfExportUtil pdfExportUtil, Context context,
      SavedStateHandle savedStateHandle) {
    return new ExamViewModel(examRepository, questionRepository, scanResultRepository, excelParser, pdfExportUtil, context, savedStateHandle);
  }
}
