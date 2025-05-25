package com.hodol.han.samples.backend.shop.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
// Restricting OpenAPI configuration to the 'dev' profile to ensure it is only enabled in
// development environments.
@Profile("dev")
public class OpenApiConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Shop API")
                .version("1.0.0")
                .description("API documentation for the Shop application")
                .contact(new Contact().name("Support Team").email("support@example.com"))
                .license(new License().name("Apache 2.0").url("http://springdoc.org")));
  }
}
