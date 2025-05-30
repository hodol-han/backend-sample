package com.hodol.han.samples.backend.shop.exception;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hodol.han.samples.backend.shop.controller.ProductController;
import com.hodol.han.samples.backend.shop.mapper.ProductMapper;
import com.hodol.han.samples.backend.shop.service.ProductService;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProductController.class)
// Disabling security filters to focus on testing exception handling logic without
// authentication/authorization concerns.
@AutoConfigureMockMvc(addFilters = false)
class GlobalExceptionHandlerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ProductService productService;

  @TestConfiguration
  static class MockConfig {
    @Bean
    public ProductService productService() {
      return mock(ProductService.class);
    }

    @Bean
    public ProductMapper productMapper() {
      return mock(ProductMapper.class);
    }
  }

  @Test
  @DisplayName("should return standard error response when product not found")
  void testNotFoundExceptionReturnsStandardErrorResponse() throws Exception {
    when(productService.getProductById(anyLong())).thenReturn(Optional.empty());
    mockMvc
        .perform(get("/api/products/999"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"))
        .andExpect(jsonPath("$.message").value("Product not found"));
  }

  @Test
  @DisplayName("should return standard error response when type mismatch")
  void testTypeMismatchReturnsStandardErrorResponse() throws Exception {
    mockMvc
        .perform(get("/api/products/abc"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value("TYPE_MISMATCH"))
        .andExpect(jsonPath("$.message").exists());
  }
}
