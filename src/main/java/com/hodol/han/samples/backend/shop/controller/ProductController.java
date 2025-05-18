package com.hodol.han.samples.backend.shop.controller;

import com.hodol.han.samples.backend.shop.dto.ProductPatchRequest;
import com.hodol.han.samples.backend.shop.dto.ProductRequest;
import com.hodol.han.samples.backend.shop.entity.Product;
import com.hodol.han.samples.backend.shop.mapper.ProductMapper;
import com.hodol.han.samples.backend.shop.service.ProductService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {

  private final ProductService productService;

  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  // Mapping logic moved to ProductMapper class

  @GetMapping
  public List<Product> getAllProducts() {
    return productService.getAllProducts();
  }

  @GetMapping("/{id}")
  public ResponseEntity<Product> getProductById(@PathVariable Long id) {
    Optional<Product> product = productService.getProductById(id);
    return product.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PostMapping
  public Product createProduct(@Valid @RequestBody ProductRequest request) {
    Product product = ProductMapper.mapToProduct(request);
    return productService.saveProduct(product);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Product> updateProduct(
      @PathVariable Long id, @Valid @RequestBody ProductRequest request) {
    Product product = ProductMapper.mapToProduct(request);
    Optional<Product> updatedProduct = productService.updateProduct(id, product);
    return updatedProduct
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PatchMapping("/{id}")
  public ResponseEntity<Product> updateProductPartial(
      @PathVariable Long id, @RequestBody ProductPatchRequest request) {
    Product product = ProductMapper.mapToProduct(request);
    Optional<Product> updatedProduct = productService.updateProductPartial(id, product);
    return updatedProduct
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
    productService.deleteProduct(id);
    return ResponseEntity.noContent().build();
  }
}
