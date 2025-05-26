package com.hodol.han.samples.backend.shop.mapper;

import com.hodol.han.samples.backend.shop.dto.CartItemResponse;
import com.hodol.han.samples.backend.shop.dto.CartResponse;
import com.hodol.han.samples.backend.shop.entity.Cart;
import com.hodol.han.samples.backend.shop.entity.CartItem;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CartMapper {

  CartMapper INSTANCE = Mappers.getMapper(CartMapper.class);

  @Mapping(source = "user.id", target = "userId")
  @Mapping(source = "cartItems", target = "items")
  CartResponse toCartResponse(Cart cart);

  List<CartItemResponse> toCartItemResponseList(List<CartItem> cartItems);

  @Mapping(source = "product.id", target = "productId")
  @Mapping(source = "product.name", target = "productName")
  @Mapping(source = "product.price", target = "unitPrice")
  @Mapping(target = "totalPrice", source = "item", qualifiedByName = "calculateTotalPrice")
  CartItemResponse toCartItemResponse(CartItem item);

  @Named("calculateTotalPrice")
  default Double calculateTotalPrice(CartItem item) {
    if (item == null
        || item.getProduct() == null
        || item.getProduct().getPrice() == null
        || item.getQuantity() < 0) {
      return 0.0;
    }

    double price = item.getProduct().getPrice();
    int quantity = item.getQuantity();
    if (price > 0 && quantity > 0 && price > Double.MAX_VALUE / quantity) {
      throw new ArithmeticException("Overflow occurred while calculating total price");
    }
    return price * quantity;
  }
}
