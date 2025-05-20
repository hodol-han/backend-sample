package com.hodol.han.samples.backend.shop.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.hodol.han.samples.backend.shop.dto.UserSignupRequest;
import com.hodol.han.samples.backend.shop.entity.User;
import com.hodol.han.samples.backend.shop.exception.DuplicateUserException;
import com.hodol.han.samples.backend.shop.repository.UserRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

class UserServiceTest {
  @Mock private UserRepository userRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @InjectMocks private UserService userService;

  // Note this test class is for unit testing only. It does not use @SpringBootTest and does not
  // perform integration testing yet. We validate manually instead in this time.
  private Validator validator;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    validator = validatorFactory.getValidator();
  }

  User signupWithValidation(UserSignupRequest request) throws ConstraintViolationException {
    Set<ConstraintViolation<UserSignupRequest>> violations = validator.validate(request);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }
    return userService.signup(request);
  }

  @Test
  @DisplayName("Should signup user successfully")
  void testSignupSuccess() {
    UserSignupRequest request = new UserSignupRequest();
    request.setUsername("testuser");
    request.setPassword("testpass");
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
    when(passwordEncoder.encode("testpass")).thenReturn("encodedpass");
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    User user = signupWithValidation(request);

    assertEquals("testuser", user.getUsername());
    assertEquals("encodedpass", user.getPassword());
    assertTrue(user.getRoles().contains("USER"));
  }

  @Test
  @DisplayName("Should throw DuplicateUserException if username exists")
  void testSignupDuplicateUser() {
    UserSignupRequest request = new UserSignupRequest();
    request.setUsername("testuser");
    request.setPassword("testpass");
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(new User()));

    assertThrows(DuplicateUserException.class, () -> signupWithValidation(request));
  }

  @Test
  @DisplayName("Should encode password only once")
  void testPasswordEncoderCalledOnce() {
    UserSignupRequest request = new UserSignupRequest();
    request.setUsername("testuser");
    request.setPassword("testpass");
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
    when(passwordEncoder.encode("testpass")).thenReturn("encodedpass");
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    signupWithValidation(request);
    verify(passwordEncoder, times(1)).encode("testpass");
  }

  @Test
  @DisplayName("Should assign USER role only")
  void testSignupAssignsUserRoleOnly() {
    UserSignupRequest request = new UserSignupRequest();
    request.setUsername("testuser");
    request.setPassword("testpass");
    when(passwordEncoder.encode(anyString())).thenReturn("encodedpass");
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    User user = signupWithValidation(request);
    assertEquals(1, user.getRoles().size());
    assertTrue(user.getRoles().contains("USER"));
  }

  @Test
  @DisplayName("Should throw exception if username is null")
  void testSignupNullUsername() {
    UserSignupRequest request = new UserSignupRequest();
    request.setUsername(null);
    request.setPassword("testpass");
    when(passwordEncoder.encode(anyString())).thenReturn("encodedpass");
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
    assertThrows(ConstraintViolationException.class, () -> signupWithValidation(request));
  }

  @Test
  @DisplayName("Should throw exception if password is null")
  void testSignupNullPassword() {
    UserSignupRequest request = new UserSignupRequest();
    request.setUsername("testuser");
    request.setPassword(null);
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
    assertThrows(ConstraintViolationException.class, () -> signupWithValidation(request));
  }
}
