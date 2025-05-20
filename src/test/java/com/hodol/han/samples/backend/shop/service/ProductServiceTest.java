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
import org.junit.jupiter.api.DisplayName;
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
  @DisplayName("should return all products when products exist")
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
  @DisplayName("should return product by id when product exists")
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
  @DisplayName("should save product when product is valid")
  void testSaveProduct() {
    Product product = new Product();
    product.setName("New Product");

    when(productRepository.save(product)).thenReturn(product);

    Product savedProduct = productService.saveProduct(product);

    assertNotNull(savedProduct);
    assertEquals("New Product", savedProduct.getName());
  }

  @Test
  @DisplayName("should delete product when id is valid")
  void testDeleteProduct() {
    Long productId = 1L;

    doNothing().when(productRepository).deleteById(productId);

    productService.deleteProduct(productId);

    verify(productRepository, times(1)).deleteById(productId);
  }

  @Test
  @DisplayName("should update product when update is valid")
  void testUpdateProduct() {
    Product existing = new Product();
    existing.setId(1L);
    existing.setName("Old");
    existing.setDescription("Old desc");
    existing.setPrice(100.0);
    existing.setStock(10);

    Product update = new Product();
    update.setName("New");
    update.setDescription("New desc");
    update.setPrice(200.0);
    update.setStock(20);

    when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(productRepository.save(existing)).thenReturn(existing);

    Optional<Product> result = productService.updateProduct(1L, update);

    assertTrue(result.isPresent());
    assertEquals("New", result.get().getName());
    assertEquals("New desc", result.get().getDescription());
    assertEquals(200.0, result.get().getPrice());
    assertEquals(20, result.get().getStock());
  }

  @Test
  @DisplayName("should update product partially when patch is valid")
  void testUpdateProductPartial() {
    Product existing = new Product();
    existing.setId(1L);
    existing.setName("Old");
    existing.setDescription("Old desc");
    existing.setPrice(100.0);
    existing.setStock(10);

    Product patch = new Product();
    patch.setName("Patched"); // Only name is patched
    // description, price, stock remain null

    when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(productRepository.save(existing)).thenReturn(existing);

    Optional<Product> result = productService.updateProductPartial(1L, patch);

    assertTrue(result.isPresent());
    assertEquals("Patched", result.get().getName());
    assertEquals("Old desc", result.get().getDescription());
    assertEquals(100.0, result.get().getPrice());
    assertEquals(10, result.get().getStock());
  }
}
