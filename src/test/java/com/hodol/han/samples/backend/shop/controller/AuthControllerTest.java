package com.hodol.han.samples.backend.shop.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hodol.han.samples.backend.shop.dto.UserSignupRequest;
import com.hodol.han.samples.backend.shop.entity.User;
import com.hodol.han.samples.backend.shop.repository.UserRepository;
import com.hodol.han.samples.backend.shop.security.JwtTokenProvider;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {
  @Autowired private MockMvc mockMvc;
  @Autowired private JwtTokenProvider jwtTokenProvider;
  @Autowired private AuthenticationManager authenticationManager;
  @Autowired private UserRepository userRepository;
  @Autowired private com.hodol.han.samples.backend.shop.service.UserService userService;

  @TestConfiguration
  static class MockConfig {
    @Bean
    public com.hodol.han.samples.backend.shop.service.UserService userService() {
      return Mockito.mock(com.hodol.han.samples.backend.shop.service.UserService.class);
    }

    @Bean
    public JwtTokenProvider jwtTokenProvider() {
      return Mockito.mock(JwtTokenProvider.class);
    }

    @Bean
    public AuthenticationManager authenticationManager() {
      return Mockito.mock(AuthenticationManager.class);
    }

    @Bean
    public UserRepository userRepository() {
      return Mockito.mock(UserRepository.class);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
      return Mockito.mock(PasswordEncoder.class);
    }
  }

  @AfterEach
  void resetMocks() {
    Mockito.reset(authenticationManager, userRepository, jwtTokenProvider);
  }

  @Test
  void signup_success() throws Exception {
    UserSignupRequest req = new UserSignupRequest();
    req.setUsername("testuser");
    req.setPassword("testpass");
    String json =
        """
      {"username":"testuser","password":"testpass"}
    """;
    mockMvc
        .perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isOk());
  }

  @Test
  void login_success() throws Exception {
    String username = "testuser";
    String password = "testpass";
    String token = "jwt.token.here";
    User user = new User();
    user.setUsername(username);
    user.setPassword(password);
    user.setRoles(Set.of("USER"));
    Authentication mockAuth = Mockito.mock(Authentication.class);
    Mockito.when(mockAuth.getName()).thenReturn(username);
    Mockito.when(authenticationManager.authenticate(Mockito.any())).thenReturn(mockAuth);
    Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.of(user));
    Mockito.when(jwtTokenProvider.createToken(Mockito.anyString(), Mockito.anySet()))
        .thenReturn(token);
    String json =
        """
      {"username":"testuser","password":"testpass"}
    """;
    mockMvc
        .perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isOk())
        .andExpect(content().string(token));
  }

  @Test
  void login_fail() throws Exception {
    String json =
        """
      {"username":"nouser","password":"wrong"}
    """;
    Mockito.when(authenticationManager.authenticate(Mockito.any()))
        .thenThrow(new AuthenticationException("fail") {});
    mockMvc
        .perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void signup_duplicate_username_fail() throws Exception {
    // Simulate duplicate username signup failure
    Mockito.doThrow(
            new com.hodol.han.samples.backend.shop.exception.DuplicateUserException("testuser"))
        .when(userService)
        .signup(Mockito.any());
    String json =
        """
      {"username":"testuser","password":"testpass"}
    """;
    mockMvc
        .perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isConflict());
  }

  @Test
  void login_nonexistent_user_fail() throws Exception {
    // Simulate login failure when user does not exist
    Mockito.when(authenticationManager.authenticate(Mockito.any()))
        .thenReturn(Mockito.mock(Authentication.class));
    Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.empty());
    String json =
        """
      {"username":"nouser","password":"testpass"}
    """;
    mockMvc
        .perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void login_wrong_password_fail() throws Exception {
    // Simulate login failure due to wrong password
    Mockito.when(authenticationManager.authenticate(Mockito.any()))
        .thenThrow(new AuthenticationException("bad credentials") {});
    String json =
        """
      {"username":"testuser","password":"wrongpass"}
    """;
    mockMvc
        .perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void signup_validation_fail() throws Exception {
    // Simulate signup validation failure (empty username and password)
    String json =
        """
      {"username":"","password":""}
    """;
    mockMvc
        .perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isBadRequest());
  }
}
