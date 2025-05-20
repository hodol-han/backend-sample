package com.hodol.han.samples.backend.shop.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.hodol.han.samples.backend.shop.validation.TrimDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

  @Bean
  public Module trimModule() {
    SimpleModule module = new SimpleModule("TrimModule");
    module.addDeserializer(String.class, new TrimDeserializer());
    return module;
  }
}
