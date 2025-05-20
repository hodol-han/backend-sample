package com.hodol.han.samples.backend.shop.service;

import com.hodol.han.samples.backend.shop.entity.Product;
import com.hodol.han.samples.backend.shop.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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

  public Page<Product> searchProducts(
      String keyword, int page, int size, String sortBy, boolean asc) {

    Sort sort = Sort.by(sortBy);

    if (!asc) {
      sort = sort.descending();
    }

    Pageable pageable = PageRequest.of(page, size, sort);
    return searchProducts(keyword, pageable);
  }

  public Page<Product> searchProducts(String keyword, Pageable pageable) {
    return productRepository.findAll(byNameOrDescription(keyword), pageable);
  }

  private Specification<Product> byNameOrDescription(String keyword) {
    String pattern = buildSearchPattern(keyword);
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.or(
            criteriaBuilder.like(root.get("name"), pattern, '\\'),
            criteriaBuilder.like(root.get("description"), pattern, '\\'));
  }

  private static String buildSearchPattern(String keyword) {
    if (keyword == null) return "%%";
    String escapedKeyword =
        keyword.trim().replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    return "%" + escapedKeyword + "%";
  }
}
