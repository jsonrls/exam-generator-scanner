package com.pbec.preboardexamchecker.data.models;

import com.google.gson.Gson;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class ListLongConverter_Factory implements Factory<ListLongConverter> {
  private final Provider<Gson> gsonProvider;

  public ListLongConverter_Factory(Provider<Gson> gsonProvider) {
    this.gsonProvider = gsonProvider;
  }

  @Override
  public ListLongConverter get() {
    return newInstance(gsonProvider.get());
  }

  public static ListLongConverter_Factory create(Provider<Gson> gsonProvider) {
    return new ListLongConverter_Factory(gsonProvider);
  }

  public static ListLongConverter newInstance(Gson gson) {
    return new ListLongConverter(gson);
  }
}
