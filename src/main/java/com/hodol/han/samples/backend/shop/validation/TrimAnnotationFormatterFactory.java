package com.hodol.han.samples.backend.shop.validation;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Formatter;
import org.springframework.format.Parser;
import org.springframework.format.Printer;
import org.springframework.lang.NonNull;

public class TrimAnnotationFormatterFactory implements AnnotationFormatterFactory<Trim> {
  @Override
  @NonNull
  public Set<Class<?>> getFieldTypes() {
    return Collections.singleton(String.class);
  }

  @Override
  @NonNull
  public Printer<String> getPrinter(@NonNull Trim annotation, @NonNull Class<?> fieldType) {
    return new Formatter<String>() {
      @Override
      public @NonNull String print(@NonNull String object, @NonNull Locale locale) {
        return object;
      }

      @Override
      public @NonNull String parse(@NonNull String text, @NonNull Locale locale) {
        return text.trim();
      }
    };
  }

  @Override
  @NonNull
  public Parser<String> getParser(@NonNull Trim annotation, @NonNull Class<?> fieldType) {
    return new Formatter<String>() {
      @Override
      public @NonNull String print(@NonNull String object, @NonNull Locale locale) {
        return object;
      }

      @Override
      public @NonNull String parse(@NonNull String text, @NonNull Locale locale) {
        return text.trim();
      }
    };
  }
}
