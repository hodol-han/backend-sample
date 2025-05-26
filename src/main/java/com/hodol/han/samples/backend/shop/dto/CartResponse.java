package com.hodol.han.samples.backend.shop.dto;

import java.util.List;
import java.util.Objects;

public class CartResponse {
  private Long id;
  private Long userId;
  private List<CartItemResponse> items;

  // private Double totalPrice; // Consider adding later

  public CartResponse() {}

  public CartResponse(Long id, Long userId, List<CartItemResponse> items) {
    this.id = id;
    this.userId = userId;
    this.items = items;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public List<CartItemResponse> getItems() {
    return items;
  }

  public void setItems(List<CartItemResponse> items) {
    this.items = items;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CartResponse that = (CartResponse) o;
    return Objects.equals(id, that.id)
        && Objects.equals(userId, that.userId)
        && Objects.equals(items, that.items);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, userId, items);
  }

  @Override
  public String toString() {
    return "CartResponse{" + "id=" + id + ", userId=" + userId + ", items=" + items + '}';
  }
}
