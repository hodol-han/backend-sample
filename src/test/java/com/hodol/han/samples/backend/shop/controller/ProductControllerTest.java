package com.hodol.han.samples.backend.shop.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hodol.han.samples.backend.shop.entity.Product;
import com.hodol.han.samples.backend.shop.service.ProductService;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ProductService productService;

  @TestConfiguration
  static class MockConfig {
    @Bean
    public ProductService productService() {
      return mock(ProductService.class);
    }
  }

  @BeforeEach
  void setUp() {
    // Removed manual MockMvc configuration as @WebMvcTest provides MockMvc automatically.
  }

  @Test
  void testGetAllProducts() throws Exception {
    Product product1 = new Product();
    product1.setId(1L);
    product1.setName("Product 1");

    Product product2 = new Product();
    product2.setId(2L);
    product2.setName("Product 2");

    when(productService.getAllProducts()).thenReturn(Arrays.asList(product1, product2));

    mockMvc
        .perform(get("/api/products"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("Product 1"))
        .andExpect(jsonPath("$[1].name").value("Product 2"));
  }

  @Test
  void testGetProductById() throws Exception {
    Product product = new Product();
    product.setId(1L);
    product.setName("Product 1");

    when(productService.getProductById(1L)).thenReturn(Optional.of(product));

    mockMvc
        .perform(get("/api/products/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Product 1"));
  }

  @Test
  void testCreateProduct() throws Exception {
    Product product = new Product();
    product.setName("New Product");

    when(productService.saveProduct(any(Product.class))).thenReturn(product);

    mockMvc
        .perform(
            post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
            {
              "name": "New Product"
            }
            """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("New Product"));

    verify(productService, times(1)).saveProduct(argThat(p -> "New Product".equals(p.getName())));
  }

  @Test
  void testDeleteProduct() throws Exception {
    doNothing().when(productService).deleteProduct(1L);

    mockMvc.perform(delete("/api/products/1")).andExpect(status().isNoContent());

    verify(productService, times(1)).deleteProduct(1L);
  }

  @Test
  void testUpdateProduct() throws Exception {
    Product updated = new Product();
    updated.setId(1L);
    updated.setName("Updated Product");
    updated.setDescription("Updated Description");
    updated.setPrice(2000.0);
    updated.setStock(5);

    when(productService.updateProduct(any(Long.class), any(Product.class)))
        .thenReturn(Optional.of(updated));

    mockMvc
        .perform(
            put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "name": "Updated Product",
                        "description": "Updated Description",
                        "price": 2000.0,
                        "stock": 5
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Updated Product"))
        .andExpect(jsonPath("$.description").value("Updated Description"))
        .andExpect(jsonPath("$.price").value(2000.0))
        .andExpect(jsonPath("$.stock").value(5));
  }

  @Test
  void testUpdateProductPartial() throws Exception {
    Product partiallyUpdated = new Product();
    partiallyUpdated.setId(1L);
    partiallyUpdated.setName("Patched Product");
    partiallyUpdated.setDescription(null); // unchanged
    partiallyUpdated.setPrice(0.0); // unchanged
    partiallyUpdated.setStock(0); // unchanged

    when(productService.updateProductPartial(any(Long.class), any(Product.class)))
        .thenReturn(Optional.of(partiallyUpdated));

    mockMvc
        .perform(
            patch("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Patched Product\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Patched Product"));
  }

  @Test
  void testUpdateProductPartialWithNulls() throws Exception {
    Product partiallyUpdated = new Product();
    partiallyUpdated.setId(1L);
    partiallyUpdated.setName(null);
    partiallyUpdated.setDescription(null);
    partiallyUpdated.setPrice(null);
    partiallyUpdated.setStock(null);

    when(productService.updateProductPartial(any(Long.class), any(Product.class)))
        .thenReturn(Optional.of(partiallyUpdated));

    mockMvc
        .perform(patch("/api/products/1").contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isOk());
  }

  @Test
  void testUpdateProductPartialWithZero() throws Exception {
    Product partiallyUpdated = new Product();
    partiallyUpdated.setId(1L);
    partiallyUpdated.setPrice(0.0);
    partiallyUpdated.setStock(0);

    when(productService.updateProductPartial(any(Long.class), any(Product.class)))
        .thenReturn(Optional.of(partiallyUpdated));

    mockMvc
        .perform(
            patch("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"price\":0.0,\"stock\":0}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.price").value(0.0))
        .andExpect(jsonPath("$.stock").value(0));
  }
}
