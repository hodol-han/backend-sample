package com.hodol.han.samples.backend.shop.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
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

import com.hodol.han.samples.backend.shop.dto.ProductDto;
import com.hodol.han.samples.backend.shop.dto.ProductPatchRequest;
import com.hodol.han.samples.backend.shop.dto.ProductRequest;
import com.hodol.han.samples.backend.shop.entity.Product;
import com.hodol.han.samples.backend.shop.mapper.ProductMapper;
import com.hodol.han.samples.backend.shop.service.ProductService;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProductController.class)
// Disabling security filters to simplify testing by bypassing authentication and authorization
// checks.
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ProductService productService;
  @Autowired private ProductMapper productMapper;

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

  @BeforeEach
  void setUp() {
    // Removed manual MockMvc configuration as @WebMvcTest provides MockMvc automatically.
    // Default mapping for Product to ProductDto
    when(productMapper.mapToProductDto(any(Product.class)))
        .thenAnswer(
            invocation -> {
              Product p = invocation.getArgument(0);
              return new ProductDto(
                  p.getId(), p.getName(), p.getDescription(), p.getPrice(), p.getStock());
            });
  }

  @Test
  @DisplayName("should return products when products exist")
  void testGetProducts() throws Exception {
    Product product1 = new Product();
    product1.setId(1L);
    product1.setName("Product 1");

    Product product2 = new Product();
    product2.setId(2L);
    product2.setName("Product 2");

    Page<Product> expected = new PageImpl<>(Arrays.asList(product1, product2));

    when(productService.searchProducts(
            eq(null),
            any(Integer.class),
            any(Integer.class),
            any(String.class),
            any(Boolean.class)))
        .thenReturn(expected);

    when(productMapper.mapToProductDto(any(Product.class)))
        .thenAnswer(
            invocation -> {
              Product src = invocation.getArgument(0);
              ProductDto dto =
                  new ProductDto(
                      src.getId(),
                      src.getName(),
                      src.getDescription(),
                      src.getPrice(),
                      src.getStock());
              return dto;
            });

    mockMvc
        .perform(get("/api/products"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].name").value("Product 1"))
        .andExpect(jsonPath("$.content[1].name").value("Product 2"));
  }

  @Test
  @DisplayName("should return product by id when product exists")
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
  @DisplayName("should create product when request is valid")
  void testCreateProduct() throws Exception {
    Product product = new Product();
    product.setName("New Product");

    when(productService.saveProduct(any(Product.class))).thenReturn(product);
    when(productMapper.mapToProduct(any(ProductRequest.class))).thenReturn(product);
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
  @DisplayName("should delete product when id is valid")
  void testDeleteProduct() throws Exception {
    doNothing().when(productService).deleteProduct(1L);

    mockMvc.perform(delete("/api/products/1")).andExpect(status().isNoContent());

    verify(productService, times(1)).deleteProduct(1L);
  }

  @Test
  @DisplayName("should update product when request is valid")
  void testUpdateProduct() throws Exception {
    Product updated = new Product();
    updated.setId(1L);
    updated.setName("Updated Product");
    updated.setDescription("Updated Description");
    updated.setPrice(2000.0);
    updated.setStock(5);

    when(productService.updateProduct(any(Long.class), any(Product.class)))
        .thenReturn(Optional.of(updated));
    when(productMapper.mapToProduct(any(ProductRequest.class))).thenReturn(updated);

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
  @DisplayName("should update product partially when patch request is valid")
  void testUpdateProductPartial() throws Exception {
    Product partiallyUpdated = new Product();
    partiallyUpdated.setId(1L);
    partiallyUpdated.setName("Patched Product");
    partiallyUpdated.setDescription(null); // unchanged
    partiallyUpdated.setPrice(0.0); // unchanged
    partiallyUpdated.setStock(0); // unchanged

    when(productService.updateProductPartial(any(Long.class), any(Product.class)))
        .thenReturn(Optional.of(partiallyUpdated));
    when(productMapper.mapToProduct(any(ProductPatchRequest.class))).thenReturn(partiallyUpdated);

    mockMvc
        .perform(
            patch("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Patched Product\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Patched Product"));
  }

  @Test
  @DisplayName("should update product partially with nulls")
  void testUpdateProductPartialWithNulls() throws Exception {
    Product partiallyUpdated = new Product();
    partiallyUpdated.setId(1L);
    partiallyUpdated.setName(null);
    partiallyUpdated.setDescription(null);
    partiallyUpdated.setPrice(null);
    partiallyUpdated.setStock(null);

    when(productService.updateProductPartial(any(Long.class), any(Product.class)))
        .thenReturn(Optional.of(partiallyUpdated));
    when(productMapper.mapToProduct(any(ProductPatchRequest.class))).thenReturn(partiallyUpdated);

    mockMvc
        .perform(patch("/api/products/1").contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("should update product partially with zero values")
  void testUpdateProductPartialWithZero() throws Exception {
    Product partiallyUpdated = new Product();
    partiallyUpdated.setId(1L);
    partiallyUpdated.setPrice(0.0);
    partiallyUpdated.setStock(0);

    when(productService.updateProductPartial(any(Long.class), any(Product.class)))
        .thenReturn(Optional.of(partiallyUpdated));
    when(productMapper.mapToProduct(any(ProductPatchRequest.class))).thenReturn(partiallyUpdated);

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
