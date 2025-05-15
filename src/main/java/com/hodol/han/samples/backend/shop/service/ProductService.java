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
}
