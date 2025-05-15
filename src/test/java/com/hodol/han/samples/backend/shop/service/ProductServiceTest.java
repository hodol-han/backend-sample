package com.hodol.han.samples.backend.shop.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hodol.han.samples.backend.shop.entity.Product;
import com.hodol.han.samples.backend.shop.repository.ProductRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ProductServiceTest {

  @Mock private ProductRepository productRepository;

  @InjectMocks private ProductService productService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testGetAllProducts() {
    Product product1 = new Product();
    product1.setId(1L);
    product1.setName("Product 1");

    Product product2 = new Product();
    product2.setId(2L);
    product2.setName("Product 2");

    when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));

    List<Product> products = productService.getAllProducts();

    assertEquals(2, products.size());
    assertEquals("Product 1", products.get(0).getName());
    assertEquals("Product 2", products.get(1).getName());
  }

  @Test
  void testGetProductById() {
    Product product = new Product();
    product.setId(1L);
    product.setName("Product 1");

    when(productRepository.findById(1L)).thenReturn(Optional.of(product));

    Optional<Product> result = productService.getProductById(1L);

    assertTrue(result.isPresent());
    assertEquals("Product 1", result.get().getName());
  }

  @Test
  void testSaveProduct() {
    Product product = new Product();
    product.setName("New Product");

    when(productRepository.save(product)).thenReturn(product);

    Product savedProduct = productService.saveProduct(product);

    assertNotNull(savedProduct);
    assertEquals("New Product", savedProduct.getName());
  }

  @Test
  void testDeleteProduct() {
    Long productId = 1L;

    doNothing().when(productRepository).deleteById(productId);

    productService.deleteProduct(productId);

    verify(productRepository, times(1)).deleteById(productId);
  }
}
