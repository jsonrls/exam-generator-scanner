package com.pbec.preboardexamchecker.domain.email;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class SmtpSlipSender_Factory implements Factory<SmtpSlipSender> {
  @Override
  public SmtpSlipSender get() {
    return newInstance();
  }

  public static SmtpSlipSender_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static SmtpSlipSender newInstance() {
    return new SmtpSlipSender();
  }

  private static final class InstanceHolder {
    private static final SmtpSlipSender_Factory INSTANCE = new SmtpSlipSender_Factory();
  }
}
