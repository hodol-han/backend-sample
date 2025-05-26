package com.hodol.han.samples.backend.shop.controller;

import com.hodol.han.samples.backend.shop.dto.CartItemRequest;
import com.hodol.han.samples.backend.shop.dto.CartResponse;
import com.hodol.han.samples.backend.shop.entity.User;
import com.hodol.han.samples.backend.shop.repository.UserRepository;
import com.hodol.han.samples.backend.shop.service.CartService;
import jakarta.validation.Valid;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

  private final CartService cartService;
  private final UserRepository userRepository;

  public CartController(CartService cartService, UserRepository userRepository) {
    this.cartService = cartService;
    this.userRepository = userRepository;
  }

  // Helper to get user ID. Adapt if your UserDetails doesn't store ID in username.
  private Long getUserId(UserDetails userDetails) {
    if (userDetails == null) {
      throw new IllegalArgumentException("UserDetails cannot be null");
    }
    String username = userDetails.getUsername();
    Optional<User> userOptional = userRepository.findByUsername(username);
    // Ensure User entity has a getId() method returning Long
    return userOptional
        .orElseThrow(
            () -> new IllegalArgumentException("User not found with username: " + username))
        .getId();
  }

  @GetMapping
  public ResponseEntity<CartResponse> getMyCart(@AuthenticationPrincipal UserDetails userDetails) {
    Long userId = getUserId(userDetails); // Placeholder for actual user ID retrieval
    return ResponseEntity.ok(cartService.getCartByUserId(userId));
  }

  @PostMapping("/items")
  public ResponseEntity<CartResponse> addItemToMyCart(
      @AuthenticationPrincipal UserDetails userDetails,
      @Valid @RequestBody CartItemRequest itemRequest) {
    Long userId = getUserId(userDetails);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(cartService.addItemToCart(userId, itemRequest));
  }

  @PutMapping("/items/{productId}")
  public ResponseEntity<CartResponse> updateMyCartItemQuantity(
      @AuthenticationPrincipal UserDetails userDetails,
      @PathVariable Long productId,
      @RequestParam int quantity) {
    Long userId = getUserId(userDetails);
    return ResponseEntity.ok(cartService.updateCartItemQuantity(userId, productId, quantity));
  }

  @DeleteMapping("/items/{productId}")
  public ResponseEntity<CartResponse> removeItemFromMyCart(
      @AuthenticationPrincipal UserDetails userDetails, @PathVariable Long productId) {
    Long userId = getUserId(userDetails);
    return ResponseEntity.ok(cartService.removeItemFromCart(userId, productId));
  }

  @DeleteMapping
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void clearMyCart(@AuthenticationPrincipal UserDetails userDetails) {
    Long userId = getUserId(userDetails);
    cartService.clearCart(userId);
  }
}
