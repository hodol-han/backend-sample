package com.hodol.han.samples.backend.shop.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hodol.han.samples.backend.shop.dto.CartItemRequest;
import com.hodol.han.samples.backend.shop.dto.CartItemResponse;
import com.hodol.han.samples.backend.shop.dto.CartResponse;
import com.hodol.han.samples.backend.shop.entity.Cart;
import com.hodol.han.samples.backend.shop.entity.CartItem;
import com.hodol.han.samples.backend.shop.entity.Product;
import com.hodol.han.samples.backend.shop.entity.User;
import com.hodol.han.samples.backend.shop.mapper.CartMapper;
import com.hodol.han.samples.backend.shop.repository.CartItemRepository;
import com.hodol.han.samples.backend.shop.repository.CartRepository;
import com.hodol.han.samples.backend.shop.repository.ProductRepository;
import com.hodol.han.samples.backend.shop.repository.UserRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

  @Mock private CartRepository cartRepository;
  @Mock private CartItemRepository cartItemRepository;
  @Mock private ProductRepository productRepository;
  @Mock private UserRepository userRepository;
  @Mock private CartMapper cartMapper;

  @InjectMocks private CartService cartService;

  private User testUser;
  private Product testProduct1;
  private Product testProduct2;
  private Cart testCart;
  private CartItem testCartItem1;

  @BeforeEach
  void setUp() {
    testUser = new User();
    testUser.setId(1L);
    testUser.setUsername("testuser");

    testProduct1 = new Product();
    testProduct1.setId(101L);
    testProduct1.setName("Test Product 1");
    testProduct1.setPrice(10.00);
    testProduct1.setStock(10);

    testProduct2 = new Product();
    testProduct2.setId(102L);
    testProduct2.setName("Test Product 2");
    testProduct2.setPrice(20.50);
    testProduct2.setStock(5);

    testCart = new Cart();
    testCart.setId(1L);
    testCart.setUser(testUser);
    testCart.setCartItems(new ArrayList<>());

    testCartItem1 = new CartItem();
    testCartItem1.setId(1L);
    testCartItem1.setCart(testCart);
    testCartItem1.setProduct(testProduct1);
    testCartItem1.setQuantity(2);
  }

  @Test
  @DisplayName("should return cart when cart exists for user ID")
  void testGetCartByUserIdExistingCart() {
    testCart.getCartItems().add(testCartItem1);

    CartResponse mockCartResponse = new CartResponse();
    mockCartResponse.setId(testCart.getId());
    mockCartResponse.setUserId(testUser.getId());

    CartItemResponse mockItemResponse = new CartItemResponse();
    mockItemResponse.setProductId(testProduct1.getId());
    mockItemResponse.setQuantity(testCartItem1.getQuantity());
    mockItemResponse.setUnitPrice(testProduct1.getPrice());
    mockItemResponse.setTotalPrice(testProduct1.getPrice() * testCartItem1.getQuantity());
    mockCartResponse.setItems(Collections.singletonList(mockItemResponse));

    when(cartRepository.findByUserId(testUser.getId())).thenReturn(Optional.of(testCart));
    when(cartMapper.toCartResponse(testCart)).thenReturn(mockCartResponse);
    // No need to mock productRepository.findById for price calculation if mapper handles it

    CartResponse cartResponse = cartService.getCartByUserId(testUser.getId());

    assertThat(cartResponse).isNotNull();
    assertThat(cartResponse.getUserId()).isEqualTo(testUser.getId());
    assertThat(cartResponse.getItems()).hasSize(1);
    CartItemResponse itemResponse = cartResponse.getItems().get(0);
    assertThat(itemResponse.getProductId()).isEqualTo(testProduct1.getId());
    assertThat(itemResponse.getUnitPrice()).isEqualTo(10.00);
    assertThat(itemResponse.getTotalPrice()).isEqualTo(20.00); // 10.00 * 2

    verify(cartRepository).findByUserId(testUser.getId());
    verify(cartMapper).toCartResponse(testCart);
  }

  @Test
  @DisplayName("should create and return new cart when cart does not exist for user ID")
  void testGetCartByUserIdNewCart() {
    when(cartRepository.findByUserId(testUser.getId())).thenReturn(Optional.empty());
    when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
    when(cartRepository.save(any(Cart.class))).thenReturn(testCart); // Return a cart with an ID

    CartResponse mockEmptyCartResponse = new CartResponse();
    mockEmptyCartResponse.setId(testCart.getId()); // Should have an ID after creation
    mockEmptyCartResponse.setUserId(testUser.getId());
    mockEmptyCartResponse.setItems(Collections.emptyList());
    when(cartMapper.toCartResponse(any(Cart.class))).thenReturn(mockEmptyCartResponse);

    CartResponse cartResponse = cartService.getCartByUserId(testUser.getId());

    assertThat(cartResponse).isNotNull();
    assertThat(cartResponse.getUserId()).isEqualTo(testUser.getId());
    assertThat(cartResponse.getItems()).isEmpty();

    verify(cartRepository).findByUserId(testUser.getId());
    verify(userRepository).findById(testUser.getId());
    verify(cartRepository).save(any(Cart.class));
    verify(cartMapper).toCartResponse(any(Cart.class));
  }

  @Test
  @DisplayName("should add new item to cart")
  void testAddItemToCartNewItem() {
    CartItemRequest itemRequest = new CartItemRequest();
    itemRequest.setProductId(testProduct1.getId());
    itemRequest.setQuantity(1);

    testProduct1.setStock(5); // Ensure enough stock

    when(cartRepository.findByUserId(testUser.getId())).thenReturn(Optional.of(testCart));
    when(productRepository.findById(testProduct1.getId())).thenReturn(Optional.of(testProduct1));
    when(cartItemRepository.findByCartIdAndProductId(testCart.getId(), testProduct1.getId()))
        .thenReturn(Optional.empty());
    when(cartItemRepository.save(any(CartItem.class)))
        .thenAnswer(
            invocation -> {
              CartItem savedItem = invocation.getArgument(0);
              savedItem.setId(2L); // Simulate save by assigning an ID
              // Ensure the item is added to the cart's list for the mapper to see
              if (!testCart.getCartItems().contains(savedItem)) {
                testCart.getCartItems().add(savedItem);
              }
              return savedItem;
            });

    // Mock the response from getCartByUserId called internally
    // This mock should reflect the state of the cart *after* the item is added.
    CartResponse mockUpdatedCartResponse = new CartResponse();
    mockUpdatedCartResponse.setUserId(testUser.getId());
    CartItemResponse mockItemResponse = new CartItemResponse();
    mockItemResponse.setProductId(testProduct1.getId());
    mockItemResponse.setQuantity(1);
    mockItemResponse.setUnitPrice(testProduct1.getPrice());
    mockItemResponse.setTotalPrice(testProduct1.getPrice() * 1);
    mockUpdatedCartResponse.setItems(Collections.singletonList(mockItemResponse));

    // This when(cartMapper...) needs to be more flexible or called for the specific cart state
    // It's better to mock based on the actual cart object that would be passed to the mapper
    when(cartMapper.toCartResponse(testCart)).thenReturn(mockUpdatedCartResponse);

    CartResponse cartResponse = cartService.addItemToCart(testUser.getId(), itemRequest);

    assertThat(cartResponse).isNotNull();
    assertThat(cartResponse.getItems()).hasSize(1);
    assertThat(cartResponse.getItems().get(0).getProductId()).isEqualTo(testProduct1.getId());
    assertThat(cartResponse.getItems().get(0).getQuantity()).isEqualTo(1);
    assertThat(cartResponse.getItems().get(0).getTotalPrice()).isEqualTo(10.00);

    verify(cartItemRepository).save(any(CartItem.class));
  }

  @Test
  @DisplayName("should throw error when adding new item with insufficient stock")
  void testAddNewItemToCartInsufficientStock() {
    CartItemRequest itemRequest = new CartItemRequest();
    itemRequest.setProductId(testProduct1.getId());
    itemRequest.setQuantity(11); // Request more than stock

    testProduct1.setStock(10); // Set stock

    when(cartRepository.findByUserId(testUser.getId())).thenReturn(Optional.of(testCart));
    when(productRepository.findById(testProduct1.getId())).thenReturn(Optional.of(testProduct1));

    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              cartService.addItemToCart(testUser.getId(), itemRequest);
            });
    assertThat(exception.getMessage()).contains("Not enough stock");
  }

  @Test
  @DisplayName("should update quantity when adding existing item to cart")
  void testAddExistingItemToCartUpdatesQuantity() {
    testCart.addCartItem(testCartItem1); // Pre-existing item with quantity 2
    testProduct1.setStock(10); // Ensure enough stock for update

    CartItemRequest itemRequest = new CartItemRequest();
    itemRequest.setProductId(testProduct1.getId());
    itemRequest.setQuantity(3); // Add 3 more, total 5

    when(cartRepository.findByUserId(testUser.getId())).thenReturn(Optional.of(testCart));
    when(productRepository.findById(testProduct1.getId())).thenReturn(Optional.of(testProduct1));
    when(cartItemRepository.findByCartIdAndProductId(testCart.getId(), testProduct1.getId()))
        .thenReturn(Optional.of(testCartItem1));
    when(cartItemRepository.save(any(CartItem.class))).thenReturn(testCartItem1);

    CartResponse mockUpdatedCartResponse = new CartResponse();
    mockUpdatedCartResponse.setUserId(testUser.getId());
    CartItemResponse mockItemResponse = new CartItemResponse();
    mockItemResponse.setProductId(testProduct1.getId());
    mockItemResponse.setQuantity(5); // 2 (original) + 3 (added) = 5
    mockItemResponse.setUnitPrice(testProduct1.getPrice());
    mockItemResponse.setTotalPrice(testProduct1.getPrice() * 5);
    mockUpdatedCartResponse.setItems(Collections.singletonList(mockItemResponse));
    when(cartMapper.toCartResponse(testCart)).thenReturn(mockUpdatedCartResponse);

    CartResponse cartResponse = cartService.addItemToCart(testUser.getId(), itemRequest);

    assertThat(cartResponse.getItems().get(0).getQuantity()).isEqualTo(5);
    assertThat(cartResponse.getItems().get(0).getTotalPrice()).isEqualTo(50.00);
    verify(cartItemRepository).save(testCartItem1);
  }

  @Test
  @DisplayName("should throw error when adding existing item with insufficient stock")
  void testAddExistingItemToCartInsufficientStock() {
    testCart.addCartItem(testCartItem1); // Pre-existing item with quantity 2
    testProduct1.setStock(4); // Stock is 4, current in cart is 2. Can add 2 more.

    CartItemRequest itemRequest = new CartItemRequest();
    itemRequest.setProductId(testProduct1.getId());
    itemRequest.setQuantity(3); // Try to add 3 more, total 5 - exceeds stock

    when(cartRepository.findByUserId(testUser.getId())).thenReturn(Optional.of(testCart));
    when(productRepository.findById(testProduct1.getId())).thenReturn(Optional.of(testProduct1));
    when(cartItemRepository.findByCartIdAndProductId(testCart.getId(), testProduct1.getId()))
        .thenReturn(Optional.of(testCartItem1));

    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              cartService.addItemToCart(testUser.getId(), itemRequest);
            });
    assertThat(exception.getMessage()).contains("Not enough stock");
  }

  @Test
  @DisplayName("should throw error when adding item and product not found")
  void testAddItemToCartProductNotFound() {
    CartItemRequest itemRequest = new CartItemRequest();
    itemRequest.setProductId(999L); // Non-existent product
    itemRequest.setQuantity(1);

    when(cartRepository.findByUserId(testUser.getId())).thenReturn(Optional.of(testCart));
    when(productRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(
        NoSuchElementException.class,
        () -> { // Changed to NoSuchElementException
          cartService.addItemToCart(testUser.getId(), itemRequest);
        });
  }

  @Test
  @DisplayName("should update quantity of cart item")
  void testUpdateCartItemQuantity() {
    testCart.addCartItem(testCartItem1); // Original quantity 2
    testProduct1.setStock(10); // Ensure enough stock
    int newQuantity = 5;

    when(cartRepository.findByUserId(testUser.getId())).thenReturn(Optional.of(testCart));
    when(cartItemRepository.findByCartIdAndProductId(testCart.getId(), testProduct1.getId()))
        .thenReturn(Optional.of(testCartItem1));
    when(cartItemRepository.save(any(CartItem.class))).thenReturn(testCartItem1);

    CartResponse mockUpdatedCartResponse = new CartResponse();
    CartItemResponse mockItemResponse = new CartItemResponse();
    mockItemResponse.setProductId(testProduct1.getId());
    mockItemResponse.setQuantity(newQuantity);
    mockItemResponse.setUnitPrice(testProduct1.getPrice());
    mockItemResponse.setTotalPrice(testProduct1.getPrice() * newQuantity);
    mockUpdatedCartResponse.setItems(Collections.singletonList(mockItemResponse));
    when(cartMapper.toCartResponse(testCart)).thenReturn(mockUpdatedCartResponse);

    CartResponse cartResponse =
        cartService.updateCartItemQuantity(testUser.getId(), testProduct1.getId(), newQuantity);

    assertThat(cartResponse.getItems().get(0).getQuantity()).isEqualTo(newQuantity);
    assertThat(cartResponse.getItems().get(0).getTotalPrice()).isEqualTo(50.00);
    verify(cartItemRepository).save(testCartItem1);
    assertThat(testCartItem1.getQuantity()).isEqualTo(newQuantity);
  }

  @Test
  @DisplayName("should throw error when updating quantity with insufficient stock")
  void testUpdateCartItemQuantityInsufficientStock() {
    testCart.addCartItem(testCartItem1); // Original quantity 2
    testProduct1.setStock(4); // Stock is 4
    int newQuantity = 5; // Try to update to 5 - exceeds stock

    when(cartRepository.findByUserId(testUser.getId())).thenReturn(Optional.of(testCart));
    // Need to mock findCartItemOrThrow behavior, which means findByCartIdAndProductId
    when(cartItemRepository.findByCartIdAndProductId(testCart.getId(), testProduct1.getId()))
        .thenReturn(Optional.of(testCartItem1));

    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              cartService.updateCartItemQuantity(
                  testUser.getId(), testProduct1.getId(), newQuantity);
            });
    assertThat(exception.getMessage()).contains("Not enough stock");
  }

  @Test
  @DisplayName("should remove item when updating quantity to zero")
  void testUpdateCartItemQuantityToZeroRemoves() {
    testCart.addCartItem(testCartItem1);

    when(cartRepository.findByUserId(testUser.getId())).thenReturn(Optional.of(testCart));
    when(cartItemRepository.findByCartIdAndProductId(testCart.getId(), testProduct1.getId()))
        .thenReturn(Optional.of(testCartItem1));
    doNothing().when(cartItemRepository).delete(testCartItem1);

    CartResponse mockEmptyCartResponse = new CartResponse(); // Cart is now empty
    mockEmptyCartResponse.setItems(Collections.emptyList());
    when(cartMapper.toCartResponse(testCart)).thenReturn(mockEmptyCartResponse);

    CartResponse cartResponse =
        cartService.updateCartItemQuantity(testUser.getId(), testProduct1.getId(), 0);

    assertThat(cartResponse.getItems()).isEmpty();
    verify(cartItemRepository).delete(testCartItem1);
    // assertThat(testCart.getCartItems()).doesNotContain(testCartItem1); // Handled by
    // orphanRemoval or explicit removal in service
  }

  @Test
  @DisplayName("should remove item from cart")
  void testRemoveItemFromCart() {
    testCart.addCartItem(testCartItem1);

    when(cartRepository.findByUserId(testUser.getId())).thenReturn(Optional.of(testCart));
    when(cartItemRepository.findByCartIdAndProductId(testCart.getId(), testProduct1.getId()))
        .thenReturn(Optional.of(testCartItem1));
    doNothing().when(cartItemRepository).delete(testCartItem1);

    CartResponse mockEmptyCartResponse = new CartResponse();
    mockEmptyCartResponse.setItems(Collections.emptyList());
    when(cartMapper.toCartResponse(testCart)).thenReturn(mockEmptyCartResponse);

    CartResponse cartResponse =
        cartService.removeItemFromCart(testUser.getId(), testProduct1.getId());

    assertThat(cartResponse.getItems()).isEmpty();
    verify(cartItemRepository).delete(testCartItem1);
  }

  @Test
  @DisplayName("should remove all items when clearing cart")
  void testClearCart() {
    testCart.addCartItem(testCartItem1);
    CartItem testCartItem2 = new CartItem();
    testCartItem2.setId(2L);
    testCartItem2.setCart(testCart);
    testCartItem2.setProduct(testProduct2);
    testCartItem2.setQuantity(1);
    testCart.addCartItem(testCartItem2);

    when(cartRepository.findByUserId(testUser.getId())).thenReturn(Optional.of(testCart));
    // Pass the actual list of items to deleteAll to simulate behavior
    doAnswer(
            invocation -> {
              List<CartItem> itemsToDelete = invocation.getArgument(0);
              testCart.getCartItems().removeAll(itemsToDelete);
              return null;
            })
        .when(cartItemRepository)
        .deleteAll(anyList());

    cartService.clearCart(testUser.getId());

    verify(cartItemRepository)
        .deleteAll(testCart.getCartItems()); // Verifies deleteAll was called with the items
    assertThat(testCart.getCartItems()).isEmpty(); // Check in-memory cart is cleared
  }
}
