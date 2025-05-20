package com.hodol.han.samples.backend.shop.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.hodol.han.samples.backend.shop.entity.User;
import com.hodol.han.samples.backend.shop.repository.UserRepository;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

class CustomUserDetailsServiceTest {

  @Mock private UserRepository userRepository;

  @InjectMocks private CustomUserDetailsService customUserDetailsService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("should return UserDetails when user exists")
  void testLoadUserByUsernameSuccess() {
    User user = new User();
    user.setUsername("testuser");
    user.setPassword("encodedpass");
    user.setRoles(Set.of("USER"));
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

    UserDetails userDetails = customUserDetailsService.loadUserByUsername("testuser");

    assertEquals("testuser", userDetails.getUsername());
    assertEquals("encodedpass", userDetails.getPassword());
    assertTrue(
        userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("USER")));
  }

  @Test
  @DisplayName("should throw UsernameNotFoundException when user does not exist")
  void testLoadUserByUsernameNotFound() {
    when(userRepository.findByUsername("nouser")).thenReturn(Optional.empty());
    assertThrows(
        UsernameNotFoundException.class,
        () -> customUserDetailsService.loadUserByUsername("nouser"));
  }

  @Test
  @DisplayName("should return empty authorities if user roles is null")
  void testLoadUserByUsernameWithNullRoles() {
    User user = new User();
    user.setUsername("testuser");
    user.setPassword("encodedpass");
    user.setRoles(null);
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

    UserDetails userDetails = customUserDetailsService.loadUserByUsername("testuser");
    assertNotNull(userDetails);
    assertTrue(userDetails.getAuthorities().isEmpty());
  }

  @Test
  @DisplayName("should return empty authorities if user roles is empty")
  void testLoadUserByUsernameWithEmptyRoles() {
    User user = new User();
    user.setUsername("testuser");
    user.setPassword("encodedpass");
    user.setRoles(Collections.emptySet());
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

    UserDetails userDetails = customUserDetailsService.loadUserByUsername("testuser");
    assertNotNull(userDetails);
    assertTrue(userDetails.getAuthorities().isEmpty());
  }

  @Test
  @DisplayName("should trim username and find user")
  void testLoadUserByUsernameWithTrimmedInput() {
    User user = new User();
    user.setUsername("testuser");
    user.setPassword("encodedpass");
    user.setRoles(Set.of("USER"));
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

    // username with leading/trailing spaces
    UserDetails userDetails = customUserDetailsService.loadUserByUsername("  testuser  ".trim());
    assertEquals("testuser", userDetails.getUsername());
  }

  @Test
  @DisplayName("should handle user with multiple roles")
  void testLoadUserByUsernameWithMultipleRoles() {
    User user = new User();
    user.setUsername("admin");
    user.setPassword("encodedpass");
    user.setRoles(Set.of("ADMIN", "USER"));
    when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));

    UserDetails userDetails = customUserDetailsService.loadUserByUsername("admin");
    assertNotNull(userDetails);
    assertTrue(
        userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN")));
    assertTrue(
        userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("USER")));
    assertEquals(2, userDetails.getAuthorities().size());
  }
}
