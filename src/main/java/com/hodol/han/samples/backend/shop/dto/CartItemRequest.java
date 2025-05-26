package com.hodol.han.samples.backend.shop.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;

public class CartItemRequest {
  @NotNull private Long productId;

  @Min(1)
  private int quantity;

  public CartItemRequest() {}

  public CartItemRequest(@NotNull Long productId, @Min(1) int quantity) {
    this.productId = productId;
    this.quantity = quantity;
  }

  public @NotNull Long getProductId() {
    return productId;
  }

  public void setProductId(@NotNull Long productId) {
    this.productId = productId;
  }

  public @Min(1) int getQuantity() {
    return quantity;
  }

  public void setQuantity(@Min(1) int quantity) {
    this.quantity = quantity;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CartItemRequest that = (CartItemRequest) o;
    return quantity == that.quantity && Objects.equals(productId, that.productId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(productId, quantity);
  }

  @Override
  public String toString() {
    return "CartItemRequest{" + "productId=" + productId + ", quantity=" + quantity + '}';
  }
}
