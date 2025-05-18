package com.hodol.han.samples.backend.shop.mapper;

import com.hodol.han.samples.backend.shop.dto.ProductPatchRequest;
import com.hodol.han.samples.backend.shop.dto.ProductRequest;
import com.hodol.han.samples.backend.shop.entity.Product;

public class ProductMapper {
  public static Product mapToProduct(ProductRequest request) {
    Product product = new Product();
    product.setName(request.getName());
    product.setDescription(request.getDescription());
    product.setPrice(request.getPrice());
    product.setStock(request.getStock());
    return product;
  }

  public static Product mapToProduct(ProductPatchRequest request) {
    Product product = new Product();
    product.setName(request.getName());
    product.setDescription(request.getDescription());
    product.setPrice(request.getPrice());
    product.setStock(request.getStock());
    return product;
  }
}
