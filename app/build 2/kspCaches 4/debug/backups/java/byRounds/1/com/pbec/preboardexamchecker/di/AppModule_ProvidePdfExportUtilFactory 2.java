package com.pbec.preboardexamchecker.di;

import android.content.Context;
import com.pbec.preboardexamchecker.utils.PdfExportUtil;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class AppModule_ProvidePdfExportUtilFactory implements Factory<PdfExportUtil> {
  private final Provider<Context> contextProvider;

  public AppModule_ProvidePdfExportUtilFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public PdfExportUtil get() {
    return providePdfExportUtil(contextProvider.get());
  }

  public static AppModule_ProvidePdfExportUtilFactory create(Provider<Context> contextProvider) {
    return new AppModule_ProvidePdfExportUtilFactory(contextProvider);
  }

  public static PdfExportUtil providePdfExportUtil(Context context) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.providePdfExportUtil(context));
  }
}
