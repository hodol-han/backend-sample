package com.hodol.han.samples.backend.shop.dto;

import java.util.Objects;

public class CartItemResponse {
  private Long id;
  private Long productId;
  private String productName; // Or full ProductDto
  private int quantity;
  private Double unitPrice;
  private Double totalPrice;

  public CartItemResponse() {}

  public CartItemResponse(
      Long id,
      Long productId,
      String productName,
      int quantity,
      Double unitPrice,
      Double totalPrice) {
    this.id = id;
    this.productId = productId;
    this.productName = productName;
    this.quantity = quantity;
    this.unitPrice = unitPrice;
    this.totalPrice = totalPrice;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public Double getUnitPrice() {
    return unitPrice;
  }

  public void setUnitPrice(Double unitPrice) {
    this.unitPrice = unitPrice;
  }

  public Double getTotalPrice() {
    return totalPrice;
  }

  public void setTotalPrice(Double totalPrice) {
    this.totalPrice = totalPrice;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CartItemResponse that = (CartItemResponse) o;
    return quantity == that.quantity
        && Objects.equals(id, that.id)
        && Objects.equals(productId, that.productId)
        && Objects.equals(productName, that.productName)
        && Objects.equals(unitPrice, that.unitPrice)
        && Objects.equals(totalPrice, that.totalPrice);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, productId, productName, quantity, unitPrice, totalPrice);
  }

  @Override
  public String toString() {
    return "CartItemResponse{"
        + "id="
        + id
        + ", productId="
        + productId
        + ", productName='"
        + productName
        + '\''
        + ", quantity="
        + quantity
        + ", unitPrice="
        + unitPrice
        + ", totalPrice="
        + totalPrice
        + '}';
  }
}
