package com.hodol.han.samples.backend.shop.service;

import com.hodol.han.samples.backend.shop.entity.Product;
import com.hodol.han.samples.backend.shop.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

  private final ProductRepository productRepository;

  public ProductService(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  public List<Product> getAllProducts() {
    return productRepository.findAll();
  }

  public Optional<Product> getProductById(Long id) {
    return productRepository.findById(id);
  }

  public Product saveProduct(Product product) {
    return productRepository.save(product);
  }

  public void deleteProduct(Long id) {
    productRepository.deleteById(id);
  }

  public Optional<Product> updateProduct(Long id, Product product) {
    return productRepository
        .findById(id)
        .map(
            existingProduct -> {
              existingProduct.setName(product.getName());
              existingProduct.setDescription(product.getDescription());
              existingProduct.setPrice(product.getPrice());
              existingProduct.setStock(product.getStock());
              return productRepository.save(existingProduct);
            });
  }

  public Optional<Product> updateProductPartial(Long id, Product product) {
    return productRepository
        .findById(id)
        .map(
            existingProduct -> {
              if (product.getName() != null) {
                existingProduct.setName(product.getName());
              }
              if (product.getDescription() != null) {
                existingProduct.setDescription(product.getDescription());
              }
              if (product.getPrice() != null) {
                existingProduct.setPrice(product.getPrice());
              }
              if (product.getStock() != null) {
                existingProduct.setStock(product.getStock());
              }
              return productRepository.save(existingProduct);
            });
  }
}
