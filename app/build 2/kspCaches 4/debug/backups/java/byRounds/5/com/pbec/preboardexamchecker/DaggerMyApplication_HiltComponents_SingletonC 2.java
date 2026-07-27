package com.pbec.preboardexamchecker;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.hilt.work.HiltWorkerFactory;
import androidx.hilt.work.WorkerAssistedFactory;
import androidx.hilt.work.WorkerFactoryModule_ProvideFactoryFactory;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.pbec.preboardexamchecker.data.AppDatabase;
import com.pbec.preboardexamchecker.data.dao.ScanResultDao;
import com.pbec.preboardexamchecker.data.repository.ExamClusterRepository;
import com.pbec.preboardexamchecker.data.repository.ExamRepository;
import com.pbec.preboardexamchecker.data.repository.QuestionRepository;
import com.pbec.preboardexamchecker.data.repository.ScanResultRepository;
import com.pbec.preboardexamchecker.data.repository.StudentRepository;
import com.pbec.preboardexamchecker.data.repository.TransactionLogRepository;
import com.pbec.preboardexamchecker.di.AppModule_ProvideFirestoreFactory;
import com.pbec.preboardexamchecker.di.AppModule_ProvidePdfExportUtilFactory;
import com.pbec.preboardexamchecker.di.DatabaseModule_ProvideAppDatabaseFactory;
import com.pbec.preboardexamchecker.di.DatabaseModule_ProvideGsonFactory;
import com.pbec.preboardexamchecker.di.DatabaseModule_ProvideScanResultDaoFactory;
import com.pbec.preboardexamchecker.di.ScoringModule_ProvideScoringStrategyFactory;
import com.pbec.preboardexamchecker.domain.email.SmtpSlipSender;
import com.pbec.preboardexamchecker.domain.excel.ClassReportExcelGenerator;
import com.pbec.preboardexamchecker.domain.excel.ExamStatsExcelGenerator;
import com.pbec.preboardexamchecker.domain.excel.PreboardRecordExcelGenerator;
import com.pbec.preboardexamchecker.domain.excel.RosterTemplateGenerator;
import com.pbec.preboardexamchecker.domain.pdf.ClassReportPdfGenerator;
import com.pbec.preboardexamchecker.domain.pdf.ExamStatsPdfGenerator;
import com.pbec.preboardexamchecker.domain.pdf.StudentSlipPdfGenerator;
import com.pbec.preboardexamchecker.domain.usecase.CalculateGwaUseCase;
import com.pbec.preboardexamchecker.domain.usecase.ComputeExamStatsUseCase;
import com.pbec.preboardexamchecker.domain.usecase.ExportClassReportUseCase;
import com.pbec.preboardexamchecker.domain.usecase.ExportStudentSlipUseCase;
import com.pbec.preboardexamchecker.ui.account.SecurityViewModel;
import com.pbec.preboardexamchecker.ui.account.SecurityViewModel_HiltModules;
import com.pbec.preboardexamchecker.ui.auth.AuthViewModel;
import com.pbec.preboardexamchecker.ui.auth.AuthViewModel_HiltModules;
import com.pbec.preboardexamchecker.ui.clusters.ClusterViewModel;
import com.pbec.preboardexamchecker.ui.clusters.ClusterViewModel_HiltModules;
import com.pbec.preboardexamchecker.ui.records.RecordsViewModel;
import com.pbec.preboardexamchecker.ui.records.RecordsViewModel_HiltModules;
import com.pbec.preboardexamchecker.ui.records.StudentRecordsRequest;
import com.pbec.preboardexamchecker.ui.records.TrashViewModel;
import com.pbec.preboardexamchecker.ui.records.TrashViewModel_HiltModules;
import com.pbec.preboardexamchecker.ui.scanner.ScannerViewModel;
import com.pbec.preboardexamchecker.ui.scanner.ScannerViewModel_HiltModules;
import com.pbec.preboardexamchecker.ui.scanner.scoring.ScoringStrategy;
import com.pbec.preboardexamchecker.ui.scanner.sync.ScanResultSyncWorker;
import com.pbec.preboardexamchecker.ui.scanner.sync.ScanResultSyncWorker_AssistedFactory;
import com.pbec.preboardexamchecker.ui.students.StudentsViewModel;
import com.pbec.preboardexamchecker.ui.students.StudentsViewModel_HiltModules;
import com.pbec.preboardexamchecker.ui.viewmodels.ExamBankViewModel;
import com.pbec.preboardexamchecker.ui.viewmodels.ExamBankViewModel_HiltModules;
import com.pbec.preboardexamchecker.ui.viewmodels.ExamContentViewModel;
import com.pbec.preboardexamchecker.ui.viewmodels.ExamContentViewModel_HiltModules;
import com.pbec.preboardexamchecker.ui.viewmodels.ExamViewModel;
import com.pbec.preboardexamchecker.ui.viewmodels.ExamViewModel_HiltModules;
import com.pbec.preboardexamchecker.ui.viewmodels.SubjectsViewModel;
import com.pbec.preboardexamchecker.ui.viewmodels.SubjectsViewModel_HiltModules;
import com.pbec.preboardexamchecker.utils.ExcelParser;
import com.pbec.preboardexamchecker.utils.PdfExportUtil;
import com.pbec.preboardexamchecker.utils.RosterExcelParser;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.IdentifierNameString;
import dagger.internal.KeepFieldType;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.SingleCheck;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

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
public final class DaggerMyApplication_HiltComponents_SingletonC {
  private DaggerMyApplication_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public MyApplication_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements MyApplication_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public MyApplication_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements MyApplication_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public MyApplication_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements MyApplication_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public MyApplication_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements MyApplication_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public MyApplication_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements MyApplication_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public MyApplication_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements MyApplication_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public MyApplication_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements MyApplication_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public MyApplication_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends MyApplication_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends MyApplication_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends MyApplication_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends MyApplication_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
      injectMainActivity2(mainActivity);
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(ImmutableMap.<String, Boolean>builderWithExpectedSize(11).put(LazyClassKeyProvider.com_pbec_preboardexamchecker_ui_auth_AuthViewModel, AuthViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_pbec_preboardexamchecker_ui_clusters_ClusterViewModel, ClusterViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_pbec_preboardexamchecker_ui_viewmodels_ExamBankViewModel, ExamBankViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_pbec_preboardexamchecker_ui_viewmodels_ExamContentViewModel, ExamContentViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_pbec_preboardexamchecker_ui_viewmodels_ExamViewModel, ExamViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_pbec_preboardexamchecker_ui_records_RecordsViewModel, RecordsViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_pbec_preboardexamchecker_ui_scanner_ScannerViewModel, ScannerViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_pbec_preboardexamchecker_ui_account_SecurityViewModel, SecurityViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_pbec_preboardexamchecker_ui_students_StudentsViewModel, StudentsViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_pbec_preboardexamchecker_ui_viewmodels_SubjectsViewModel, SubjectsViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_pbec_preboardexamchecker_ui_records_TrashViewModel, TrashViewModel_HiltModules.KeyModule.provide()).build());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @CanIgnoreReturnValue
    private MainActivity injectMainActivity2(MainActivity instance) {
      MainActivity_MembersInjector.injectExamRepositoryInterface(instance, singletonCImpl.examRepositoryProvider.get());
      return instance;
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_pbec_preboardexamchecker_ui_clusters_ClusterViewModel = "com.pbec.preboardexamchecker.ui.clusters.ClusterViewModel";

      static String com_pbec_preboardexamchecker_ui_account_SecurityViewModel = "com.pbec.preboardexamchecker.ui.account.SecurityViewModel";

      static String com_pbec_preboardexamchecker_ui_viewmodels_SubjectsViewModel = "com.pbec.preboardexamchecker.ui.viewmodels.SubjectsViewModel";

      static String com_pbec_preboardexamchecker_ui_students_StudentsViewModel = "com.pbec.preboardexamchecker.ui.students.StudentsViewModel";

      static String com_pbec_preboardexamchecker_ui_viewmodels_ExamContentViewModel = "com.pbec.preboardexamchecker.ui.viewmodels.ExamContentViewModel";

      static String com_pbec_preboardexamchecker_ui_viewmodels_ExamBankViewModel = "com.pbec.preboardexamchecker.ui.viewmodels.ExamBankViewModel";

      static String com_pbec_preboardexamchecker_ui_auth_AuthViewModel = "com.pbec.preboardexamchecker.ui.auth.AuthViewModel";

      static String com_pbec_preboardexamchecker_ui_records_RecordsViewModel = "com.pbec.preboardexamchecker.ui.records.RecordsViewModel";

      static String com_pbec_preboardexamchecker_ui_scanner_ScannerViewModel = "com.pbec.preboardexamchecker.ui.scanner.ScannerViewModel";

      static String com_pbec_preboardexamchecker_ui_viewmodels_ExamViewModel = "com.pbec.preboardexamchecker.ui.viewmodels.ExamViewModel";

      static String com_pbec_preboardexamchecker_ui_records_TrashViewModel = "com.pbec.preboardexamchecker.ui.records.TrashViewModel";

      @KeepFieldType
      ClusterViewModel com_pbec_preboardexamchecker_ui_clusters_ClusterViewModel2;

      @KeepFieldType
      SecurityViewModel com_pbec_preboardexamchecker_ui_account_SecurityViewModel2;

      @KeepFieldType
      SubjectsViewModel com_pbec_preboardexamchecker_ui_viewmodels_SubjectsViewModel2;

      @KeepFieldType
      StudentsViewModel com_pbec_preboardexamchecker_ui_students_StudentsViewModel2;

      @KeepFieldType
      ExamContentViewModel com_pbec_preboardexamchecker_ui_viewmodels_ExamContentViewModel2;

      @KeepFieldType
      ExamBankViewModel com_pbec_preboardexamchecker_ui_viewmodels_ExamBankViewModel2;

      @KeepFieldType
      AuthViewModel com_pbec_preboardexamchecker_ui_auth_AuthViewModel2;

      @KeepFieldType
      RecordsViewModel com_pbec_preboardexamchecker_ui_records_RecordsViewModel2;

      @KeepFieldType
      ScannerViewModel com_pbec_preboardexamchecker_ui_scanner_ScannerViewModel2;

      @KeepFieldType
      ExamViewModel com_pbec_preboardexamchecker_ui_viewmodels_ExamViewModel2;

      @KeepFieldType
      TrashViewModel com_pbec_preboardexamchecker_ui_records_TrashViewModel2;
    }
  }

  private static final class ViewModelCImpl extends MyApplication_HiltComponents.ViewModelC {
    private final SavedStateHandle savedStateHandle;

    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<AuthViewModel> authViewModelProvider;

    private Provider<ClusterViewModel> clusterViewModelProvider;

    private Provider<ExamBankViewModel> examBankViewModelProvider;

    private Provider<ExamContentViewModel> examContentViewModelProvider;

    private Provider<ExamViewModel> examViewModelProvider;

    private Provider<RecordsViewModel> recordsViewModelProvider;

    private Provider<ScannerViewModel> scannerViewModelProvider;

    private Provider<SecurityViewModel> securityViewModelProvider;

    private Provider<StudentsViewModel> studentsViewModelProvider;

    private Provider<SubjectsViewModel> subjectsViewModelProvider;

    private Provider<TrashViewModel> trashViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.savedStateHandle = savedStateHandleParam;
      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    private ComputeExamStatsUseCase computeExamStatsUseCase() {
      return new ComputeExamStatsUseCase(singletonCImpl.examRepositoryProvider.get(), singletonCImpl.questionRepositoryProvider.get());
    }

    private ExportClassReportUseCase exportClassReportUseCase() {
      return new ExportClassReportUseCase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.classReportPdfGeneratorProvider.get());
    }

    private ExportStudentSlipUseCase exportStudentSlipUseCase() {
      return new ExportStudentSlipUseCase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.studentSlipPdfGeneratorProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.authViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.clusterViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.examBankViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.examContentViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.examViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.recordsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
      this.scannerViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 6);
      this.securityViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 7);
      this.studentsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 8);
      this.subjectsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 9);
      this.trashViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 10);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(ImmutableMap.<String, javax.inject.Provider<ViewModel>>builderWithExpectedSize(11).put(LazyClassKeyProvider.com_pbec_preboardexamchecker_ui_auth_AuthViewModel, ((Provider) authViewModelProvider)).put(LazyClassKeyProvider.com_pbec_preboardexamchecker_ui_clusters_ClusterViewModel, ((Provider) clusterViewModelProvider)).put(LazyClassKeyProvider.com_pbec_preboardexamchecker_ui_viewmodels_ExamBankViewModel, ((Provider) examBankViewModelProvider)).put(LazyClassKeyProvider.com_pbec_preboardexamchecker_ui_viewmodels_ExamContentViewModel, ((Provider) examContentViewModelProvider)).put(LazyClassKeyProvider.com_pbec_preboardexamchecker_ui_viewmodels_ExamViewModel, ((Provider) examViewModelProvider)).put(LazyClassKeyProvider.com_pbec_preboardexamchecker_ui_records_RecordsViewModel, ((Provider) recordsViewModelProvider)).put(LazyClassKeyProvider.com_pbec_preboardexamchecker_ui_scanner_ScannerViewModel, ((Provider) scannerViewModelProvider)).put(LazyClassKeyProvider.com_pbec_preboardexamchecker_ui_account_SecurityViewModel, ((Provider) securityViewModelProvider)).put(LazyClassKeyProvider.com_pbec_preboardexamchecker_ui_students_StudentsViewModel, ((Provider) studentsViewModelProvider)).put(LazyClassKeyProvider.com_pbec_preboardexamchecker_ui_viewmodels_SubjectsViewModel, ((Provider) subjectsViewModelProvider)).put(LazyClassKeyProvider.com_pbec_preboardexamchecker_ui_records_TrashViewModel, ((Provider) trashViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return ImmutableMap.<Class<?>, Object>of();
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_pbec_preboardexamchecker_ui_students_StudentsViewModel = "com.pbec.preboardexamchecker.ui.students.StudentsViewModel";

      static String com_pbec_preboardexamchecker_ui_viewmodels_ExamContentViewModel = "com.pbec.preboardexamchecker.ui.viewmodels.ExamContentViewModel";

      static String com_pbec_preboardexamchecker_ui_records_TrashViewModel = "com.pbec.preboardexamchecker.ui.records.TrashViewModel";

      static String com_pbec_preboardexamchecker_ui_viewmodels_ExamViewModel = "com.pbec.preboardexamchecker.ui.viewmodels.ExamViewModel";

      static String com_pbec_preboardexamchecker_ui_viewmodels_ExamBankViewModel = "com.pbec.preboardexamchecker.ui.viewmodels.ExamBankViewModel";

      static String com_pbec_preboardexamchecker_ui_auth_AuthViewModel = "com.pbec.preboardexamchecker.ui.auth.AuthViewModel";

      static String com_pbec_preboardexamchecker_ui_clusters_ClusterViewModel = "com.pbec.preboardexamchecker.ui.clusters.ClusterViewModel";

      static String com_pbec_preboardexamchecker_ui_records_RecordsViewModel = "com.pbec.preboardexamchecker.ui.records.RecordsViewModel";

      static String com_pbec_preboardexamchecker_ui_viewmodels_SubjectsViewModel = "com.pbec.preboardexamchecker.ui.viewmodels.SubjectsViewModel";

      static String com_pbec_preboardexamchecker_ui_scanner_ScannerViewModel = "com.pbec.preboardexamchecker.ui.scanner.ScannerViewModel";

      static String com_pbec_preboardexamchecker_ui_account_SecurityViewModel = "com.pbec.preboardexamchecker.ui.account.SecurityViewModel";

      @KeepFieldType
      StudentsViewModel com_pbec_preboardexamchecker_ui_students_StudentsViewModel2;

      @KeepFieldType
      ExamContentViewModel com_pbec_preboardexamchecker_ui_viewmodels_ExamContentViewModel2;

      @KeepFieldType
      TrashViewModel com_pbec_preboardexamchecker_ui_records_TrashViewModel2;

      @KeepFieldType
      ExamViewModel com_pbec_preboardexamchecker_ui_viewmodels_ExamViewModel2;

      @KeepFieldType
      ExamBankViewModel com_pbec_preboardexamchecker_ui_viewmodels_ExamBankViewModel2;

      @KeepFieldType
      AuthViewModel com_pbec_preboardexamchecker_ui_auth_AuthViewModel2;

      @KeepFieldType
      ClusterViewModel com_pbec_preboardexamchecker_ui_clusters_ClusterViewModel2;

      @KeepFieldType
      RecordsViewModel com_pbec_preboardexamchecker_ui_records_RecordsViewModel2;

      @KeepFieldType
      SubjectsViewModel com_pbec_preboardexamchecker_ui_viewmodels_SubjectsViewModel2;

      @KeepFieldType
      ScannerViewModel com_pbec_preboardexamchecker_ui_scanner_ScannerViewModel2;

      @KeepFieldType
      SecurityViewModel com_pbec_preboardexamchecker_ui_account_SecurityViewModel2;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.pbec.preboardexamchecker.ui.auth.AuthViewModel 
          return (T) new AuthViewModel(singletonCImpl.provideFirestoreProvider.get(), ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 1: // com.pbec.preboardexamchecker.ui.clusters.ClusterViewModel 
          return (T) new ClusterViewModel(singletonCImpl.examClusterRepositoryProvider.get(), singletonCImpl.examRepositoryProvider.get());

          case 2: // com.pbec.preboardexamchecker.ui.viewmodels.ExamBankViewModel 
          return (T) new ExamBankViewModel(singletonCImpl.questionRepositoryProvider.get(), singletonCImpl.examRepositoryProvider.get(), singletonCImpl.transactionLogRepositoryProvider.get(), new ExcelParser(), ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), viewModelCImpl.savedStateHandle);

          case 3: // com.pbec.preboardexamchecker.ui.viewmodels.ExamContentViewModel 
          return (T) new ExamContentViewModel(singletonCImpl.examRepositoryProvider.get(), singletonCImpl.questionRepositoryProvider.get(), singletonCImpl.providePdfExportUtilProvider.get(), ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), viewModelCImpl.savedStateHandle);

          case 4: // com.pbec.preboardexamchecker.ui.viewmodels.ExamViewModel 
          return (T) new ExamViewModel(singletonCImpl.examRepositoryProvider.get(), singletonCImpl.questionRepositoryProvider.get(), singletonCImpl.scanResultRepositoryProvider.get(), new ExcelParser(), singletonCImpl.providePdfExportUtilProvider.get(), ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), viewModelCImpl.savedStateHandle);

          case 5: // com.pbec.preboardexamchecker.ui.records.RecordsViewModel 
          return (T) new RecordsViewModel(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.scanResultRepositoryProvider.get(), new CalculateGwaUseCase(), viewModelCImpl.computeExamStatsUseCase(), viewModelCImpl.exportClassReportUseCase(), viewModelCImpl.exportStudentSlipUseCase(), singletonCImpl.examStatsPdfGeneratorProvider.get(), singletonCImpl.examStatsExcelGeneratorProvider.get(), singletonCImpl.classReportExcelGeneratorProvider.get(), singletonCImpl.examClusterRepositoryProvider.get(), singletonCImpl.preboardRecordExcelGeneratorProvider.get(), singletonCImpl.studentRepositoryProvider.get(), singletonCImpl.smtpSlipSenderProvider.get(), singletonCImpl.studentRecordsRequestProvider.get());

          case 6: // com.pbec.preboardexamchecker.ui.scanner.ScannerViewModel 
          return (T) new ScannerViewModel(singletonCImpl.studentRepositoryProvider.get(), singletonCImpl.examRepositoryProvider.get(), singletonCImpl.examClusterRepositoryProvider.get(), singletonCImpl.scanResultRepositoryProvider.get(), singletonCImpl.provideScoringStrategyProvider.get(), singletonCImpl.questionRepositoryProvider.get(), ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 7: // com.pbec.preboardexamchecker.ui.account.SecurityViewModel 
          return (T) new SecurityViewModel(singletonCImpl.provideFirestoreProvider.get(), ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 8: // com.pbec.preboardexamchecker.ui.students.StudentsViewModel 
          return (T) new StudentsViewModel(singletonCImpl.provideFirestoreProvider.get(), new RosterExcelParser(), singletonCImpl.rosterTemplateGeneratorProvider.get(), singletonCImpl.preboardRecordExcelGeneratorProvider.get(), singletonCImpl.scanResultRepositoryProvider.get(), singletonCImpl.examClusterRepositoryProvider.get(), new CalculateGwaUseCase(), singletonCImpl.studentRecordsRequestProvider.get(), ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 9: // com.pbec.preboardexamchecker.ui.viewmodels.SubjectsViewModel 
          return (T) new SubjectsViewModel();

          case 10: // com.pbec.preboardexamchecker.ui.records.TrashViewModel 
          return (T) new TrashViewModel(singletonCImpl.scanResultRepositoryProvider.get(), singletonCImpl.examRepositoryProvider.get(), singletonCImpl.questionRepositoryProvider.get(), singletonCImpl.provideFirestoreProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends MyApplication_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends MyApplication_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }
  }

  private static final class SingletonCImpl extends MyApplication_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<Gson> provideGsonProvider;

    private Provider<AppDatabase> provideAppDatabaseProvider;

    private Provider<FirebaseFirestore> provideFirestoreProvider;

    private Provider<ExamRepository> examRepositoryProvider;

    private Provider<ScanResultRepository> scanResultRepositoryProvider;

    private Provider<ScanResultSyncWorker_AssistedFactory> scanResultSyncWorker_AssistedFactoryProvider;

    private Provider<ExamClusterRepository> examClusterRepositoryProvider;

    private Provider<QuestionRepository> questionRepositoryProvider;

    private Provider<TransactionLogRepository> transactionLogRepositoryProvider;

    private Provider<PdfExportUtil> providePdfExportUtilProvider;

    private Provider<ClassReportPdfGenerator> classReportPdfGeneratorProvider;

    private Provider<StudentSlipPdfGenerator> studentSlipPdfGeneratorProvider;

    private Provider<ExamStatsPdfGenerator> examStatsPdfGeneratorProvider;

    private Provider<ExamStatsExcelGenerator> examStatsExcelGeneratorProvider;

    private Provider<ClassReportExcelGenerator> classReportExcelGeneratorProvider;

    private Provider<PreboardRecordExcelGenerator> preboardRecordExcelGeneratorProvider;

    private Provider<StudentRepository> studentRepositoryProvider;

    private Provider<SmtpSlipSender> smtpSlipSenderProvider;

    private Provider<StudentRecordsRequest> studentRecordsRequestProvider;

    private Provider<ScoringStrategy> provideScoringStrategyProvider;

    private Provider<RosterTemplateGenerator> rosterTemplateGeneratorProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    private ScanResultDao scanResultDao() {
      return DatabaseModule_ProvideScanResultDaoFactory.provideScanResultDao(provideAppDatabaseProvider.get());
    }

    private Map<String, javax.inject.Provider<WorkerAssistedFactory<? extends ListenableWorker>>> mapOfStringAndProviderOfWorkerAssistedFactoryOf(
        ) {
      return ImmutableMap.<String, javax.inject.Provider<WorkerAssistedFactory<? extends ListenableWorker>>>of("com.pbec.preboardexamchecker.ui.scanner.sync.ScanResultSyncWorker", ((Provider) scanResultSyncWorker_AssistedFactoryProvider));
    }

    private HiltWorkerFactory hiltWorkerFactory() {
      return WorkerFactoryModule_ProvideFactoryFactory.provideFactory(mapOfStringAndProviderOfWorkerAssistedFactoryOf());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.provideGsonProvider = DoubleCheck.provider(new SwitchingProvider<Gson>(singletonCImpl, 3));
      this.provideAppDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<AppDatabase>(singletonCImpl, 2));
      this.provideFirestoreProvider = DoubleCheck.provider(new SwitchingProvider<FirebaseFirestore>(singletonCImpl, 4));
      this.examRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<ExamRepository>(singletonCImpl, 5));
      this.scanResultRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<ScanResultRepository>(singletonCImpl, 1));
      this.scanResultSyncWorker_AssistedFactoryProvider = SingleCheck.provider(new SwitchingProvider<ScanResultSyncWorker_AssistedFactory>(singletonCImpl, 0));
      this.examClusterRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<ExamClusterRepository>(singletonCImpl, 6));
      this.questionRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<QuestionRepository>(singletonCImpl, 7));
      this.transactionLogRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<TransactionLogRepository>(singletonCImpl, 8));
      this.providePdfExportUtilProvider = DoubleCheck.provider(new SwitchingProvider<PdfExportUtil>(singletonCImpl, 9));
      this.classReportPdfGeneratorProvider = DoubleCheck.provider(new SwitchingProvider<ClassReportPdfGenerator>(singletonCImpl, 10));
      this.studentSlipPdfGeneratorProvider = DoubleCheck.provider(new SwitchingProvider<StudentSlipPdfGenerator>(singletonCImpl, 11));
      this.examStatsPdfGeneratorProvider = DoubleCheck.provider(new SwitchingProvider<ExamStatsPdfGenerator>(singletonCImpl, 12));
      this.examStatsExcelGeneratorProvider = DoubleCheck.provider(new SwitchingProvider<ExamStatsExcelGenerator>(singletonCImpl, 13));
      this.classReportExcelGeneratorProvider = DoubleCheck.provider(new SwitchingProvider<ClassReportExcelGenerator>(singletonCImpl, 14));
      this.preboardRecordExcelGeneratorProvider = DoubleCheck.provider(new SwitchingProvider<PreboardRecordExcelGenerator>(singletonCImpl, 15));
      this.studentRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<StudentRepository>(singletonCImpl, 16));
      this.smtpSlipSenderProvider = DoubleCheck.provider(new SwitchingProvider<SmtpSlipSender>(singletonCImpl, 17));
      this.studentRecordsRequestProvider = DoubleCheck.provider(new SwitchingProvider<StudentRecordsRequest>(singletonCImpl, 18));
      this.provideScoringStrategyProvider = DoubleCheck.provider(new SwitchingProvider<ScoringStrategy>(singletonCImpl, 19));
      this.rosterTemplateGeneratorProvider = DoubleCheck.provider(new SwitchingProvider<RosterTemplateGenerator>(singletonCImpl, 20));
    }

    @Override
    public void injectMyApplication(MyApplication myApplication) {
      injectMyApplication2(myApplication);
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return ImmutableSet.<Boolean>of();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    @CanIgnoreReturnValue
    private MyApplication injectMyApplication2(MyApplication instance) {
      MyApplication_MembersInjector.injectWorkerFactory(instance, hiltWorkerFactory());
      return instance;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.pbec.preboardexamchecker.ui.scanner.sync.ScanResultSyncWorker_AssistedFactory 
          return (T) new ScanResultSyncWorker_AssistedFactory() {
            @Override
            public ScanResultSyncWorker create(Context context, WorkerParameters params) {
              return new ScanResultSyncWorker(context, params, singletonCImpl.scanResultRepositoryProvider.get());
            }
          };

          case 1: // com.pbec.preboardexamchecker.data.repository.ScanResultRepository 
          return (T) new ScanResultRepository(singletonCImpl.scanResultDao(), singletonCImpl.provideFirestoreProvider.get(), singletonCImpl.examRepositoryProvider.get(), ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 2: // com.pbec.preboardexamchecker.data.AppDatabase 
          return (T) DatabaseModule_ProvideAppDatabaseFactory.provideAppDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.provideGsonProvider.get());

          case 3: // com.google.gson.Gson 
          return (T) DatabaseModule_ProvideGsonFactory.provideGson();

          case 4: // com.google.firebase.firestore.FirebaseFirestore 
          return (T) AppModule_ProvideFirestoreFactory.provideFirestore();

          case 5: // com.pbec.preboardexamchecker.data.repository.ExamRepository 
          return (T) new ExamRepository(singletonCImpl.provideFirestoreProvider.get(), ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 6: // com.pbec.preboardexamchecker.data.repository.ExamClusterRepository 
          return (T) new ExamClusterRepository(singletonCImpl.provideFirestoreProvider.get());

          case 7: // com.pbec.preboardexamchecker.data.repository.QuestionRepository 
          return (T) new QuestionRepository(singletonCImpl.provideFirestoreProvider.get(), ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 8: // com.pbec.preboardexamchecker.data.repository.TransactionLogRepository 
          return (T) new TransactionLogRepository(singletonCImpl.provideFirestoreProvider.get());

          case 9: // com.pbec.preboardexamchecker.utils.PdfExportUtil 
          return (T) AppModule_ProvidePdfExportUtilFactory.providePdfExportUtil(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 10: // com.pbec.preboardexamchecker.domain.pdf.ClassReportPdfGenerator 
          return (T) new ClassReportPdfGenerator();

          case 11: // com.pbec.preboardexamchecker.domain.pdf.StudentSlipPdfGenerator 
          return (T) new StudentSlipPdfGenerator();

          case 12: // com.pbec.preboardexamchecker.domain.pdf.ExamStatsPdfGenerator 
          return (T) new ExamStatsPdfGenerator();

          case 13: // com.pbec.preboardexamchecker.domain.excel.ExamStatsExcelGenerator 
          return (T) new ExamStatsExcelGenerator();

          case 14: // com.pbec.preboardexamchecker.domain.excel.ClassReportExcelGenerator 
          return (T) new ClassReportExcelGenerator();

          case 15: // com.pbec.preboardexamchecker.domain.excel.PreboardRecordExcelGenerator 
          return (T) new PreboardRecordExcelGenerator();

          case 16: // com.pbec.preboardexamchecker.data.repository.StudentRepository 
          return (T) new StudentRepository(singletonCImpl.provideFirestoreProvider.get());

          case 17: // com.pbec.preboardexamchecker.domain.email.SmtpSlipSender 
          return (T) new SmtpSlipSender();

          case 18: // com.pbec.preboardexamchecker.ui.records.StudentRecordsRequest 
          return (T) new StudentRecordsRequest();

          case 19: // com.pbec.preboardexamchecker.ui.scanner.scoring.ScoringStrategy 
          return (T) ScoringModule_ProvideScoringStrategyFactory.provideScoringStrategy();

          case 20: // com.pbec.preboardexamchecker.domain.excel.RosterTemplateGenerator 
          return (T) new RosterTemplateGenerator();

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
