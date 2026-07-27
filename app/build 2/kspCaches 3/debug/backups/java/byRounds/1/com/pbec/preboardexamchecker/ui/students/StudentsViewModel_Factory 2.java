package com.pbec.preboardexamchecker.ui.students;

import android.content.Context;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pbec.preboardexamchecker.data.repository.ExamClusterRepository;
import com.pbec.preboardexamchecker.data.repository.IScanResultRepository;
import com.pbec.preboardexamchecker.domain.excel.PreboardRecordExcelGenerator;
import com.pbec.preboardexamchecker.domain.excel.RosterTemplateGenerator;
import com.pbec.preboardexamchecker.domain.usecase.CalculateGwaUseCase;
import com.pbec.preboardexamchecker.ui.records.StudentRecordsRequest;
import com.pbec.preboardexamchecker.utils.RosterExcelParser;
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
public final class StudentsViewModel_Factory implements Factory<StudentsViewModel> {
  private final Provider<FirebaseFirestore> firestoreProvider;

  private final Provider<RosterExcelParser> rosterParserProvider;

  private final Provider<RosterTemplateGenerator> templateGeneratorProvider;

  private final Provider<PreboardRecordExcelGenerator> recordGeneratorProvider;

  private final Provider<IScanResultRepository> scanResultRepositoryProvider;

  private final Provider<ExamClusterRepository> clusterRepositoryProvider;

  private final Provider<CalculateGwaUseCase> calculateGwaProvider;

  private final Provider<StudentRecordsRequest> studentRecordsRequestProvider;

  private final Provider<Context> contextProvider;

  public StudentsViewModel_Factory(Provider<FirebaseFirestore> firestoreProvider,
      Provider<RosterExcelParser> rosterParserProvider,
      Provider<RosterTemplateGenerator> templateGeneratorProvider,
      Provider<PreboardRecordExcelGenerator> recordGeneratorProvider,
      Provider<IScanResultRepository> scanResultRepositoryProvider,
      Provider<ExamClusterRepository> clusterRepositoryProvider,
      Provider<CalculateGwaUseCase> calculateGwaProvider,
      Provider<StudentRecordsRequest> studentRecordsRequestProvider,
      Provider<Context> contextProvider) {
    this.firestoreProvider = firestoreProvider;
    this.rosterParserProvider = rosterParserProvider;
    this.templateGeneratorProvider = templateGeneratorProvider;
    this.recordGeneratorProvider = recordGeneratorProvider;
    this.scanResultRepositoryProvider = scanResultRepositoryProvider;
    this.clusterRepositoryProvider = clusterRepositoryProvider;
    this.calculateGwaProvider = calculateGwaProvider;
    this.studentRecordsRequestProvider = studentRecordsRequestProvider;
    this.contextProvider = contextProvider;
  }

  @Override
  public StudentsViewModel get() {
    return newInstance(firestoreProvider.get(), rosterParserProvider.get(), templateGeneratorProvider.get(), recordGeneratorProvider.get(), scanResultRepositoryProvider.get(), clusterRepositoryProvider.get(), calculateGwaProvider.get(), studentRecordsRequestProvider.get(), contextProvider.get());
  }

  public static StudentsViewModel_Factory create(Provider<FirebaseFirestore> firestoreProvider,
      Provider<RosterExcelParser> rosterParserProvider,
      Provider<RosterTemplateGenerator> templateGeneratorProvider,
      Provider<PreboardRecordExcelGenerator> recordGeneratorProvider,
      Provider<IScanResultRepository> scanResultRepositoryProvider,
      Provider<ExamClusterRepository> clusterRepositoryProvider,
      Provider<CalculateGwaUseCase> calculateGwaProvider,
      Provider<StudentRecordsRequest> studentRecordsRequestProvider,
      Provider<Context> contextProvider) {
    return new StudentsViewModel_Factory(firestoreProvider, rosterParserProvider, templateGeneratorProvider, recordGeneratorProvider, scanResultRepositoryProvider, clusterRepositoryProvider, calculateGwaProvider, studentRecordsRequestProvider, contextProvider);
  }

  public static StudentsViewModel newInstance(FirebaseFirestore firestore,
      RosterExcelParser rosterParser, RosterTemplateGenerator templateGenerator,
      PreboardRecordExcelGenerator recordGenerator, IScanResultRepository scanResultRepository,
      ExamClusterRepository clusterRepository, CalculateGwaUseCase calculateGwa,
      StudentRecordsRequest studentRecordsRequest, Context context) {
    return new StudentsViewModel(firestore, rosterParser, templateGenerator, recordGenerator, scanResultRepository, clusterRepository, calculateGwa, studentRecordsRequest, context);
  }
}
