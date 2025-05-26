package com.hodol.han.samples.backend.shop.service;

import com.hodol.han.samples.backend.shop.dto.CartItemRequest;
import com.hodol.han.samples.backend.shop.dto.CartResponse;
import com.hodol.han.samples.backend.shop.entity.*;
import com.hodol.han.samples.backend.shop.mapper.CartMapper;
import com.hodol.han.samples.backend.shop.repository.CartItemRepository;
import com.hodol.han.samples.backend.shop.repository.CartRepository;
import com.hodol.han.samples.backend.shop.repository.ProductRepository;
import com.hodol.han.samples.backend.shop.repository.UserRepository;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CartService {

  private final CartRepository cartRepository;
  private final CartItemRepository cartItemRepository;
  private final ProductRepository productRepository;
  private final UserRepository userRepository;
  private final CartMapper cartMapper;

  public CartService(
      CartRepository cartRepository,
      CartItemRepository cartItemRepository,
      ProductRepository productRepository,
      UserRepository userRepository,
      CartMapper cartMapper) {
    this.cartRepository = cartRepository;
    this.cartItemRepository = cartItemRepository;
    this.productRepository = productRepository;
    this.userRepository = userRepository;
    this.cartMapper = cartMapper;
  }

  @Transactional(readOnly = true)
  public CartResponse getCartByUserId(Long userId) {
    Cart cart = cartRepository.findByUserId(userId).orElseGet(() -> createCartForUser(userId));
    return cartMapper.toCartResponse(cart);
  }

  public CartResponse addItemToCart(Long userId, CartItemRequest itemRequest) {
    Cart cart = cartRepository.findByUserId(userId).orElseGet(() -> createCartForUser(userId));
    Product product =
        productRepository
            .findById(itemRequest.getProductId())
            .orElseThrow(
                () ->
                    new NoSuchElementException(
                        "Product not found with id: " + itemRequest.getProductId()));

    if (product.getStock() < itemRequest.getQuantity()) {
      throw new IllegalArgumentException("Not enough stock for product: " + product.getName());
    }

    CartItem cartItem =
        cartItemRepository
            .findByCartIdAndProductId(cart.getId(), product.getId())
            .orElse(new CartItem());

    if (cartItem.getId() == null) { // New item
      cartItem.setCart(cart);
      cartItem.setProduct(product);
      cartItem.setQuantity(itemRequest.getQuantity());
      cart.addCartItem(cartItem);
    } else { // Existing item, update quantity
      if (product.getStock() < cartItem.getQuantity() + itemRequest.getQuantity()) {
        throw new IllegalArgumentException("Not enough stock for product: " + product.getName());
      }
      cartItem.setQuantity(cartItem.getQuantity() + itemRequest.getQuantity());
    }
    cartItemRepository.save(cartItem);
    return getCartByUserId(userId);
  }

  public CartResponse updateCartItemQuantity(Long userId, Long productId, int quantity) {
    if (quantity <= 0) {
      return removeItemFromCart(userId, productId);
    }
    Cart cart = findCartByUserIdOrThrow(userId);
    CartItem cartItem = findCartItemOrThrow(cart.getId(), productId);
    Product product = cartItem.getProduct();

    if (product.getStock() < quantity) {
      throw new IllegalArgumentException("Not enough stock for product: " + product.getName());
    }

    cartItem.setQuantity(quantity);
    cartItemRepository.save(cartItem);
    return getCartByUserId(userId);
  }

  public CartResponse removeItemFromCart(Long userId, Long productId) {
    Cart cart = findCartByUserIdOrThrow(userId);
    CartItem cartItem = findCartItemOrThrow(cart.getId(), productId);
    cart.removeCartItem(cartItem);
    cartItemRepository.delete(cartItem);
    return getCartByUserId(userId);
  }

  public void clearCart(Long userId) {
    Cart cart = findCartByUserIdOrThrow(userId);
    cartItemRepository.deleteAll(cart.getCartItems());
    cart.getCartItems().clear();
  }

  private Cart createCartForUser(Long userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));
    Cart newCart = new Cart();
    newCart.setUser(user);
    return cartRepository.save(newCart);
  }

  private Cart findCartByUserIdOrThrow(Long userId) {
    return cartRepository
        .findByUserId(userId)
        .orElseThrow(() -> new NoSuchElementException("Cart not found with id: " + userId));
  }

  private CartItem findCartItemOrThrow(Long cartId, Long productId) {
    return cartItemRepository
        .findByCartIdAndProductId(cartId, productId)
        .orElseThrow(
            () ->
                new NoSuchElementException(
                    "Product not found in cart with CartId: "
                        + cartId
                        + ", ProductId: "
                        + productId));
  }
}
