package com.pbec.preboardexamchecker.ui.records;

import android.content.Context;
import com.pbec.preboardexamchecker.data.repository.ExamClusterRepository;
import com.pbec.preboardexamchecker.data.repository.IScanResultRepository;
import com.pbec.preboardexamchecker.data.repository.IStudentRepository;
import com.pbec.preboardexamchecker.domain.email.SmtpSlipSender;
import com.pbec.preboardexamchecker.domain.excel.ClassReportExcelGenerator;
import com.pbec.preboardexamchecker.domain.excel.ExamStatsExcelGenerator;
import com.pbec.preboardexamchecker.domain.excel.PreboardRecordExcelGenerator;
import com.pbec.preboardexamchecker.domain.pdf.ExamStatsPdfGenerator;
import com.pbec.preboardexamchecker.domain.usecase.CalculateGwaUseCase;
import com.pbec.preboardexamchecker.domain.usecase.ComputeExamStatsUseCase;
import com.pbec.preboardexamchecker.domain.usecase.ExportClassReportUseCase;
import com.pbec.preboardexamchecker.domain.usecase.ExportStudentSlipUseCase;
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
public final class RecordsViewModel_Factory implements Factory<RecordsViewModel> {
  private final Provider<Context> contextProvider;

  private final Provider<IScanResultRepository> repositoryProvider;

  private final Provider<CalculateGwaUseCase> calculateGwaProvider;

  private final Provider<ComputeExamStatsUseCase> computeExamStatsProvider;

  private final Provider<ExportClassReportUseCase> exportClassReportProvider;

  private final Provider<ExportStudentSlipUseCase> exportStudentSlipProvider;

  private final Provider<ExamStatsPdfGenerator> examStatsPdfProvider;

  private final Provider<ExamStatsExcelGenerator> examStatsExcelProvider;

  private final Provider<ClassReportExcelGenerator> classReportExcelProvider;

  private final Provider<ExamClusterRepository> clusterRepositoryProvider;

  private final Provider<PreboardRecordExcelGenerator> preboardRecordExcelProvider;

  private final Provider<IStudentRepository> studentRepositoryProvider;

  private final Provider<SmtpSlipSender> slipSenderProvider;

  private final Provider<StudentRecordsRequest> studentRecordsRequestProvider;

  public RecordsViewModel_Factory(Provider<Context> contextProvider,
      Provider<IScanResultRepository> repositoryProvider,
      Provider<CalculateGwaUseCase> calculateGwaProvider,
      Provider<ComputeExamStatsUseCase> computeExamStatsProvider,
      Provider<ExportClassReportUseCase> exportClassReportProvider,
      Provider<ExportStudentSlipUseCase> exportStudentSlipProvider,
      Provider<ExamStatsPdfGenerator> examStatsPdfProvider,
      Provider<ExamStatsExcelGenerator> examStatsExcelProvider,
      Provider<ClassReportExcelGenerator> classReportExcelProvider,
      Provider<ExamClusterRepository> clusterRepositoryProvider,
      Provider<PreboardRecordExcelGenerator> preboardRecordExcelProvider,
      Provider<IStudentRepository> studentRepositoryProvider,
      Provider<SmtpSlipSender> slipSenderProvider,
      Provider<StudentRecordsRequest> studentRecordsRequestProvider) {
    this.contextProvider = contextProvider;
    this.repositoryProvider = repositoryProvider;
    this.calculateGwaProvider = calculateGwaProvider;
    this.computeExamStatsProvider = computeExamStatsProvider;
    this.exportClassReportProvider = exportClassReportProvider;
    this.exportStudentSlipProvider = exportStudentSlipProvider;
    this.examStatsPdfProvider = examStatsPdfProvider;
    this.examStatsExcelProvider = examStatsExcelProvider;
    this.classReportExcelProvider = classReportExcelProvider;
    this.clusterRepositoryProvider = clusterRepositoryProvider;
    this.preboardRecordExcelProvider = preboardRecordExcelProvider;
    this.studentRepositoryProvider = studentRepositoryProvider;
    this.slipSenderProvider = slipSenderProvider;
    this.studentRecordsRequestProvider = studentRecordsRequestProvider;
  }

  @Override
  public RecordsViewModel get() {
    return newInstance(contextProvider.get(), repositoryProvider.get(), calculateGwaProvider.get(), computeExamStatsProvider.get(), exportClassReportProvider.get(), exportStudentSlipProvider.get(), examStatsPdfProvider.get(), examStatsExcelProvider.get(), classReportExcelProvider.get(), clusterRepositoryProvider.get(), preboardRecordExcelProvider.get(), studentRepositoryProvider.get(), slipSenderProvider.get(), studentRecordsRequestProvider.get());
  }

  public static RecordsViewModel_Factory create(Provider<Context> contextProvider,
      Provider<IScanResultRepository> repositoryProvider,
      Provider<CalculateGwaUseCase> calculateGwaProvider,
      Provider<ComputeExamStatsUseCase> computeExamStatsProvider,
      Provider<ExportClassReportUseCase> exportClassReportProvider,
      Provider<ExportStudentSlipUseCase> exportStudentSlipProvider,
      Provider<ExamStatsPdfGenerator> examStatsPdfProvider,
      Provider<ExamStatsExcelGenerator> examStatsExcelProvider,
      Provider<ClassReportExcelGenerator> classReportExcelProvider,
      Provider<ExamClusterRepository> clusterRepositoryProvider,
      Provider<PreboardRecordExcelGenerator> preboardRecordExcelProvider,
      Provider<IStudentRepository> studentRepositoryProvider,
      Provider<SmtpSlipSender> slipSenderProvider,
      Provider<StudentRecordsRequest> studentRecordsRequestProvider) {
    return new RecordsViewModel_Factory(contextProvider, repositoryProvider, calculateGwaProvider, computeExamStatsProvider, exportClassReportProvider, exportStudentSlipProvider, examStatsPdfProvider, examStatsExcelProvider, classReportExcelProvider, clusterRepositoryProvider, preboardRecordExcelProvider, studentRepositoryProvider, slipSenderProvider, studentRecordsRequestProvider);
  }

  public static RecordsViewModel newInstance(Context context, IScanResultRepository repository,
      CalculateGwaUseCase calculateGwa, ComputeExamStatsUseCase computeExamStats,
      ExportClassReportUseCase exportClassReport, ExportStudentSlipUseCase exportStudentSlip,
      ExamStatsPdfGenerator examStatsPdf, ExamStatsExcelGenerator examStatsExcel,
      ClassReportExcelGenerator classReportExcel, ExamClusterRepository clusterRepository,
      PreboardRecordExcelGenerator preboardRecordExcel, IStudentRepository studentRepository,
      SmtpSlipSender slipSender, StudentRecordsRequest studentRecordsRequest) {
    return new RecordsViewModel(context, repository, calculateGwa, computeExamStats, exportClassReport, exportStudentSlip, examStatsPdf, examStatsExcel, classReportExcel, clusterRepository, preboardRecordExcel, studentRepository, slipSender, studentRecordsRequest);
  }
}
