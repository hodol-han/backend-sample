package com.hodol.han.samples.backend.shop.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "carts")
public class Cart extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private User user;

  @OneToMany(
      mappedBy = "cart",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private List<CartItem> cartItems = new ArrayList<>();

  // Default constructor (JPA requirement)
  public Cart() {}

  // Getter and Setter for id
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  // Getter and Setter for user
  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  // Getter and Setter for cartItems
  public List<CartItem> getCartItems() {
    return cartItems;
  }

  public void setCartItems(List<CartItem> cartItems) {
    this.cartItems = cartItems;
  }

  // Helper methods to manage cart items
  public void addCartItem(CartItem cartItem) {
    cartItems.add(cartItem);
    cartItem.setCart(this);
  }

  public void removeCartItem(CartItem cartItem) {
    cartItems.remove(cartItem);
    cartItem.setCart(null);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Cart cart = (Cart) o;
    // If IDs are null (e.g., for new entities not yet persisted),
    // they are not equal unless they are the same instance.
    // If IDs are not null, compare by ID.
    if (id == null) {
      // For transient instances, fall back to super.equals() if id is null for both,
      // or consider them different if one has id and other doesn't,
      // or if both are transient but not the same instance.
      return cart.id == null && super.equals(o);
    }
    return Objects.equals(id, cart.id);
  }

  @Override
  public int hashCode() {
    // Use a prime number for hashing if ID is null, or the ID's hashcode.
    // System.identityHashCode(this) can be used for transient objects.
    return id != null ? Objects.hash(id) : System.identityHashCode(this);
  }

  @Override
  public String toString() {
    return "Cart{"
        + "id="
        + id
        +
        // Avoid printing user directly to prevent potential circular toString calls
        ", userId="
        + (user != null ? user.getId() : "null")
        + // user.getId() is generally safe for proxies
        ", cartItems=[lazy collection]"
        + // Indicate that cartItems is a lazy-loaded collection
        ", createdAt="
        + (getCreatedAt() != null ? getCreatedAt().toString() : "null")
        + // from BaseTimeEntity
        ", updatedAt="
        + (getUpdatedAt() != null ? getUpdatedAt().toString() : "null")
        + // from BaseTimeEntity
        '}';
  }
}
