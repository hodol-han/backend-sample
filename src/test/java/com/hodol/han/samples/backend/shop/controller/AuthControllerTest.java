package com.hodol.han.samples.backend.shop.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hodol.han.samples.backend.shop.config.JacksonConfig;
import com.hodol.han.samples.backend.shop.config.WebConfig;
import com.hodol.han.samples.backend.shop.dto.UserSignupRequest;
import com.hodol.han.samples.backend.shop.entity.User;
import com.hodol.han.samples.backend.shop.security.JwtTokenProvider;
import com.hodol.han.samples.backend.shop.service.UserService;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@Import({JacksonConfig.class, WebConfig.class})
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {
  @Autowired private MockMvc mockMvc;
  @Autowired private JwtTokenProvider jwtTokenProvider;
  @Autowired private AuthenticationManager authenticationManager;
  @Autowired private UserService userService;

  @TestConfiguration
  static class MockConfig {
    @Bean
    public UserService userService() {
      return Mockito.mock(UserService.class);
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
    public PasswordEncoder passwordEncoder() {
      return Mockito.mock(PasswordEncoder.class);
    }
  }

  @AfterEach
  void resetMocks() {
    Mockito.reset(authenticationManager, jwtTokenProvider, userService);
  }

  @Test
  @DisplayName("should signup user when request is valid")
  void testSignupSuccess() throws Exception {
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
  @DisplayName("should return token when login is successful")
  void testLoginSuccess() throws Exception {
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
    Mockito.when(jwtTokenProvider.generateToken(mockAuth)).thenReturn(token);
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
  @DisplayName("should return unauthorized when login fails")
  void testLoginFail() throws Exception {
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
  @DisplayName("should return conflict when signup with duplicate username")
  void testSignupDuplicateUsernameFail() throws Exception {
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
  @DisplayName("should return unauthorized when login with nonexistent user")
  void testLoginNonexistentUserFail() throws Exception {
    // Simulate login failure when user does not exist
    Mockito.when(authenticationManager.authenticate(Mockito.any()))
        .thenThrow(new AuthenticationException("nonexistent user") {});
    String json =
        """
      {"username":"nouser","password":"testpass"}
    """;
    mockMvc
        .perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("should return unauthorized when login with wrong password")
  void testLoginWrongPasswordFail() throws Exception {
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
  @DisplayName("should return bad request when signup validation fails")
  void testSignupValidationFail() throws Exception {
    // Simulate signup validation failure (empty username and password)
    String json =
        """
      {"username":"","password":""}
    """;
    mockMvc
        .perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("should trim username when signup")
  void testSignupTrimUsername() throws Exception {
    String json =
        """
      {"username":"  testuser  ","password":"testpass"}
    """;
    mockMvc
        .perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isOk());
    ArgumentCaptor<UserSignupRequest> captor = ArgumentCaptor.forClass(UserSignupRequest.class);
    verify(userService).signup(captor.capture());
    assertEquals("testuser", captor.getValue().getUsername());
  }

  @Test
  @DisplayName("should trim username when login")
  void testLoginTrimUsername() throws Exception {
    String token = "jwt.token.trim";
    Authentication mockAuth = Mockito.mock(Authentication.class);
    Mockito.when(mockAuth.getName()).thenReturn("testuser");
    Mockito.when(authenticationManager.authenticate(Mockito.any())).thenReturn(mockAuth);
    User user = new User();
    user.setUsername("testuser");
    user.setPassword("pass");
    user.setRoles(Set.of("USER"));
    Mockito.when(jwtTokenProvider.generateToken(mockAuth)).thenReturn(token);

    String json =
        """
      {"username":"  testuser  ","password":"pass"}
    """;
    mockMvc
        .perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isOk())
        .andExpect(content().string(token));

    ArgumentCaptor<UsernamePasswordAuthenticationToken> authCaptor =
        ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
    verify(authenticationManager).authenticate(authCaptor.capture());
    UsernamePasswordAuthenticationToken arg = authCaptor.getValue();
    assertEquals("testuser", arg.getPrincipal());
    assertEquals("pass", arg.getCredentials());
  }
}
