package com.pbec.preboardexamchecker.ui.viewmodels;

import android.content.Context;
import androidx.lifecycle.SavedStateHandle;
import com.pbec.preboardexamchecker.data.repository.ExamRepository;
import com.pbec.preboardexamchecker.data.repository.QuestionRepository;
import com.pbec.preboardexamchecker.data.repository.TransactionLogRepository;
import com.pbec.preboardexamchecker.utils.ExcelParser;
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
public final class ExamBankViewModel_Factory implements Factory<ExamBankViewModel> {
  private final Provider<QuestionRepository> questionRepositoryProvider;

  private final Provider<ExamRepository> examRepositoryProvider;

  private final Provider<TransactionLogRepository> transactionLogRepositoryProvider;

  private final Provider<ExcelParser> excelParserProvider;

  private final Provider<Context> contextProvider;

  private final Provider<SavedStateHandle> savedStateHandleProvider;

  public ExamBankViewModel_Factory(Provider<QuestionRepository> questionRepositoryProvider,
      Provider<ExamRepository> examRepositoryProvider,
      Provider<TransactionLogRepository> transactionLogRepositoryProvider,
      Provider<ExcelParser> excelParserProvider, Provider<Context> contextProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    this.questionRepositoryProvider = questionRepositoryProvider;
    this.examRepositoryProvider = examRepositoryProvider;
    this.transactionLogRepositoryProvider = transactionLogRepositoryProvider;
    this.excelParserProvider = excelParserProvider;
    this.contextProvider = contextProvider;
    this.savedStateHandleProvider = savedStateHandleProvider;
  }

  @Override
  public ExamBankViewModel get() {
    return newInstance(questionRepositoryProvider.get(), examRepositoryProvider.get(), transactionLogRepositoryProvider.get(), excelParserProvider.get(), contextProvider.get(), savedStateHandleProvider.get());
  }

  public static ExamBankViewModel_Factory create(
      Provider<QuestionRepository> questionRepositoryProvider,
      Provider<ExamRepository> examRepositoryProvider,
      Provider<TransactionLogRepository> transactionLogRepositoryProvider,
      Provider<ExcelParser> excelParserProvider, Provider<Context> contextProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    return new ExamBankViewModel_Factory(questionRepositoryProvider, examRepositoryProvider, transactionLogRepositoryProvider, excelParserProvider, contextProvider, savedStateHandleProvider);
  }

  public static ExamBankViewModel newInstance(QuestionRepository questionRepository,
      ExamRepository examRepository, TransactionLogRepository transactionLogRepository,
      ExcelParser excelParser, Context context, SavedStateHandle savedStateHandle) {
    return new ExamBankViewModel(questionRepository, examRepository, transactionLogRepository, excelParser, context, savedStateHandle);
  }
}
