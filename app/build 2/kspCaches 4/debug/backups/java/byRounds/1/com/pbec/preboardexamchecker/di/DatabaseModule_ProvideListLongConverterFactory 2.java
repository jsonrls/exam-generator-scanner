package com.pbec.preboardexamchecker.di;

import com.google.gson.Gson;
import com.pbec.preboardexamchecker.data.models.ListLongConverter;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class DatabaseModule_ProvideListLongConverterFactory implements Factory<ListLongConverter> {
  private final Provider<Gson> gsonProvider;

  public DatabaseModule_ProvideListLongConverterFactory(Provider<Gson> gsonProvider) {
    this.gsonProvider = gsonProvider;
  }

  @Override
  public ListLongConverter get() {
    return provideListLongConverter(gsonProvider.get());
  }

  public static DatabaseModule_ProvideListLongConverterFactory create(Provider<Gson> gsonProvider) {
    return new DatabaseModule_ProvideListLongConverterFactory(gsonProvider);
  }

  public static ListLongConverter provideListLongConverter(Gson gson) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideListLongConverter(gson));
  }
}
