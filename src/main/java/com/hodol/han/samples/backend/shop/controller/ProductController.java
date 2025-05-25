package com.hodol.han.samples.backend.shop.controller;

import com.hodol.han.samples.backend.shop.dto.PagedResponse;
import com.hodol.han.samples.backend.shop.dto.ProductDto;
import com.hodol.han.samples.backend.shop.dto.ProductPatchRequest;
import com.hodol.han.samples.backend.shop.dto.ProductRequest;
import com.hodol.han.samples.backend.shop.entity.Product;
import com.hodol.han.samples.backend.shop.mapper.ProductMapper;
import com.hodol.han.samples.backend.shop.service.ProductService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/products")
public class ProductController {

  private static final String PRODUCT_NOT_FOUND_MESSAGE = "Product not found";

  private final ProductService productService;
  private final ProductMapper productMapper;

  public ProductController(ProductService productService, ProductMapper productMapper) {
    this.productService = productService;
    this.productMapper = productMapper;
  }

  @GetMapping
  public ResponseEntity<PagedResponse<ProductDto>> listProducts(
      @RequestParam(required = false) String q,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "id") String sortBy,
      @RequestParam(defaultValue = "false") boolean asc) {

    Page<Product> result = productService.searchProducts(q, page, size, sortBy, asc);
    List<ProductDto> products =
        result.getContent().stream().map(productMapper::mapToProductDto).toList();

    return ResponseEntity.ok(
        new PagedResponse<>(
            products, result.getNumber(), result.getSize(), result.getTotalElements()));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
    ProductDto dto =
        productService
            .getProductById(id)
            .map(productMapper::mapToProductDto)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, PRODUCT_NOT_FOUND_MESSAGE));
    return ResponseEntity.ok(dto);
  }

  @PostMapping
  public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody ProductRequest request) {
    Product product = productMapper.mapToProduct(request);
    Product saved = productService.saveProduct(product);
    ProductDto dto = productMapper.mapToProductDto(saved);
    return ResponseEntity.ok(dto);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ProductDto> updateProduct(
      @PathVariable Long id, @Valid @RequestBody ProductRequest request) {
    ProductDto dto =
        productService
            .updateProduct(id, productMapper.mapToProduct(request))
            .map(productMapper::mapToProductDto)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, PRODUCT_NOT_FOUND_MESSAGE));
    return ResponseEntity.ok(dto);
  }

  @PatchMapping("/{id}")
  public ResponseEntity<ProductDto> updateProductPartial(
      @PathVariable Long id, @RequestBody ProductPatchRequest request) {
    ProductDto dto =
        productService
            .updateProductPartial(id, productMapper.mapToProduct(request))
            .map(productMapper::mapToProductDto)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, PRODUCT_NOT_FOUND_MESSAGE));
    return ResponseEntity.ok(dto);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
    productService
        .getProductById(id)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, PRODUCT_NOT_FOUND_MESSAGE));
    productService.deleteProduct(id);
    return ResponseEntity.noContent().build();
  }
}
