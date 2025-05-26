package com.hodol.han.samples.backend.shop.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hodol.han.samples.backend.shop.dto.CartItemRequest;
import com.hodol.han.samples.backend.shop.dto.CartItemResponse;
import com.hodol.han.samples.backend.shop.dto.CartResponse;
import com.hodol.han.samples.backend.shop.repository.UserRepository;
import com.hodol.han.samples.backend.shop.service.CartService;
import java.util.Collections;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CartController.class)
// Disabling security filters to simplify testing by bypassing authentication and authorization
// checks.
@AutoConfigureMockMvc(addFilters = false)
class CartControllerTest {

  @TestConfiguration
  static class TestConfig {
    @Bean
    public CartService cartService() {
      return mock(CartService.class);
    }

    @Bean
    public UserRepository userRepository() {
      return mock(UserRepository.class);
    }
  }

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private CartService cartService;

  @Autowired private UserRepository userRepository;

  private CartResponse cartResponse;
  private final Long MOCK_USER_ID = 1L;

  @BeforeEach
  void setUp() {
    cartResponse = new CartResponse();
    cartResponse.setId(1L);
    cartResponse.setUserId(MOCK_USER_ID);

    CartItemResponse itemResponse = new CartItemResponse();
    itemResponse.setId(1L);
    itemResponse.setProductId(101L);
    itemResponse.setProductName("Test Product");
    itemResponse.setQuantity(2);
    itemResponse.setUnitPrice(10.00);
    itemResponse.setTotalPrice(20.00);
    cartResponse.setItems(Collections.singletonList(itemResponse));

    // Create and set up Mock User object
    com.hodol.han.samples.backend.shop.entity.User mockedUserEntity =
        mock(com.hodol.han.samples.backend.shop.entity.User.class);
    when(mockedUserEntity.getId()).thenReturn(MOCK_USER_ID);
    // Set username to "1" to match @WithMockUser(username = "1")
    when(userRepository.findByUsername("1")).thenReturn(java.util.Optional.of(mockedUserEntity));
  }

  @Test
  @DisplayName("should return cart on successful get")
  @WithMockUser(username = "1")
  void testGetMyCartSuccess() throws Exception {
    when(cartService.getCartByUserId(MOCK_USER_ID)).thenReturn(cartResponse);

    mockMvc
        .perform(get("/api/v1/cart"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.userId").value(MOCK_USER_ID))
        .andExpect(jsonPath("$.items[0].productId").value(101L))
        .andExpect(jsonPath("$.items[0].unitPrice").value(10.00))
        .andExpect(jsonPath("$.items[0].totalPrice").value(20.00));
  }

  @Test
  @DisplayName("should return updated cart on successful add")
  @WithMockUser(username = "1")
  void testAddItemToCartSuccess() throws Exception {
    CartItemRequest itemRequest = new CartItemRequest();
    itemRequest.setProductId(102L);
    itemRequest.setQuantity(1);

    CartResponse updatedCartResponse = new CartResponse();
    updatedCartResponse.setId(1L);
    updatedCartResponse.setUserId(MOCK_USER_ID);
    CartItemResponse newItemResponse = new CartItemResponse();
    newItemResponse.setProductId(102L);
    newItemResponse.setQuantity(1);
    newItemResponse.setUnitPrice(15.00);
    newItemResponse.setTotalPrice(15.00);
    updatedCartResponse.setItems(Collections.singletonList(newItemResponse));

    when(cartService.addItemToCart(anyLong(), any(CartItemRequest.class)))
        .thenReturn(updatedCartResponse);

    mockMvc
        .perform(
            post("/api/v1/cart/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.userId").value(MOCK_USER_ID))
        .andExpect(jsonPath("$.items[0].productId").value(102L))
        .andExpect(jsonPath("$.items[0].unitPrice").value(15.00))
        .andExpect(jsonPath("$.items[0].totalPrice").value(15.00));
  }

  @Test
  @DisplayName("should throw error on add with insufficient stock")
  @WithMockUser(username = "1")
  void testAddItemToCartNotEnoughStock() throws Exception {
    CartItemRequest itemRequest = new CartItemRequest();
    itemRequest.setProductId(103L);
    itemRequest.setQuantity(100); // Requesting high quantity

    when(cartService.addItemToCart(anyLong(), any(CartItemRequest.class)))
        .thenThrow(new IllegalArgumentException("Not enough stock for product 103"));

    mockMvc
        .perform(
            post("/api/v1/cart/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Not enough stock for product 103"));
  }

  @Test
  @DisplayName("should throw error on add with product not found")
  @WithMockUser(username = "1")
  void testAddItemToCartProductNotFound() throws Exception {
    CartItemRequest itemRequest = new CartItemRequest();
    itemRequest.setProductId(999L); // Non-existent product
    itemRequest.setQuantity(1);

    when(cartService.addItemToCart(anyLong(), any(CartItemRequest.class)))
        .thenThrow(new NoSuchElementException("Product not found with id: 999"));

    mockMvc
        .perform(
            post("/api/v1/cart/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemRequest)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Product not found with id: 999"));
  }

  @Test
  @DisplayName("should return updated cart on successful quantity update")
  @WithMockUser(username = "1")
  void testUpdateCartItemQuantitySuccess() throws Exception {
    Long productIdToUpdate = 101L;
    int newQuantity = 5;

    // Simulate cart response after quantity update
    // Create a new CartResponse for the updated state to avoid modifying the shared cartResponse
    CartResponse updatedCartResponse = new CartResponse();
    updatedCartResponse.setId(cartResponse.getId());
    updatedCartResponse.setUserId(cartResponse.getUserId());
    CartItemResponse updatedItem = new CartItemResponse();
    updatedItem.setId(cartResponse.getItems().get(0).getId());
    updatedItem.setProductId(cartResponse.getItems().get(0).getProductId());
    updatedItem.setProductName(cartResponse.getItems().get(0).getProductName());
    updatedItem.setQuantity(newQuantity);
    updatedItem.setUnitPrice(cartResponse.getItems().get(0).getUnitPrice());
    updatedItem.setTotalPrice(updatedItem.getUnitPrice() * newQuantity);
    updatedCartResponse.setItems(Collections.singletonList(updatedItem));

    when(cartService.updateCartItemQuantity(MOCK_USER_ID, productIdToUpdate, newQuantity))
        .thenReturn(updatedCartResponse);

    mockMvc
        .perform(
            put("/api/v1/cart/items/{productId}", productIdToUpdate)
                .param("quantity", String.valueOf(newQuantity)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items[0].quantity").value(newQuantity))
        .andExpect(jsonPath("$.items[0].totalPrice").value(50.00));
  }

  @Test
  @DisplayName("should throw error on quantity update with insufficient stock")
  @WithMockUser(username = "1")
  void testUpdateCartItemQuantityNotEnoughStock() throws Exception {
    Long productIdToUpdate = 101L;
    int newQuantity = 100; // Requesting high quantity

    when(cartService.updateCartItemQuantity(MOCK_USER_ID, productIdToUpdate, newQuantity))
        .thenThrow(new IllegalArgumentException("Not enough stock for product 101"));

    mockMvc
        .perform(
            put("/api/v1/cart/items/{productId}", productIdToUpdate)
                .param("quantity", String.valueOf(newQuantity)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Not enough stock for product 101"));
  }

  @Test
  @DisplayName("should throw error on quantity update with product not found")
  @WithMockUser(username = "1")
  void testUpdateCartItemQuantityProductNotFound() throws Exception {
    Long productIdToUpdate = 999L; // Non-existent product
    int newQuantity = 5;

    when(cartService.updateCartItemQuantity(MOCK_USER_ID, productIdToUpdate, newQuantity))
        .thenThrow(new NoSuchElementException("Cart item not found for product id: 999"));

    mockMvc
        .perform(
            put("/api/v1/cart/items/{productId}", productIdToUpdate)
                .param("quantity", String.valueOf(newQuantity)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Cart item not found for product id: 999"));
  }

  @Test
  @DisplayName("should return updated cart on successful item removal")
  @WithMockUser(username = "1")
  void testRemoveItemFromCartSuccess() throws Exception {
    Long productIdToRemove = 101L;
    CartResponse cartAfterRemoval = new CartResponse();
    cartAfterRemoval.setId(1L);
    cartAfterRemoval.setUserId(MOCK_USER_ID);
    cartAfterRemoval.setItems(Collections.emptyList());

    when(cartService.removeItemFromCart(MOCK_USER_ID, productIdToRemove))
        .thenReturn(cartAfterRemoval);

    mockMvc
        .perform(delete("/api/v1/cart/items/{productId}", productIdToRemove))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items").isEmpty());
  }

  @Test
  @DisplayName("should throw error on item removal with item not found")
  @WithMockUser(username = "1")
  void testRemoveItemFromCartItemNotFound() throws Exception {
    Long productIdToRemove = 999L; // Non-existent product

    when(cartService.removeItemFromCart(MOCK_USER_ID, productIdToRemove))
        .thenThrow(new NoSuchElementException("Cart item not found for product id: 999"));

    mockMvc
        .perform(delete("/api/v1/cart/items/{productId}", productIdToRemove))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Cart item not found for product id: 999"));
  }

  @Test
  @DisplayName("should return no content on successful clear cart")
  @WithMockUser(username = "1")
  void testClearCartSuccess() throws Exception {
    doNothing().when(cartService).clearCart(MOCK_USER_ID);

    mockMvc.perform(delete("/api/v1/cart")).andExpect(status().isNoContent());
  }
}
