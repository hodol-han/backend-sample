package com.hodol.han.samples.backend.shop.mapper;

import com.hodol.han.samples.backend.shop.dto.ProductPatchRequest;
import com.hodol.han.samples.backend.shop.dto.ProductRequest;
import com.hodol.han.samples.backend.shop.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
  @Mapping(target = "id", ignore = true)
  public Product mapToProduct(ProductRequest req);

  @Mapping(target = "id", ignore = true)
  public Product mapToProduct(ProductPatchRequest req);
}
