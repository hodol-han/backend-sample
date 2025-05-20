package com.hodol.han.samples.backend.shop.config;

import com.hodol.han.samples.backend.shop.validation.TrimAnnotationFormatterFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addFormatters(@NonNull FormatterRegistry registry) {
    // Register custom Trim annotation formatter for explicit trimming
    registry.addFormatterForFieldAnnotation(new TrimAnnotationFormatterFactory());
  }
}
