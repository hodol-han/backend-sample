package com.hodol.han.samples.backend.shop.controller;

import com.hodol.han.samples.backend.shop.dto.UserLoginRequest;
import com.hodol.han.samples.backend.shop.dto.UserSignupRequest;
import com.hodol.han.samples.backend.shop.entity.User;
import com.hodol.han.samples.backend.shop.repository.UserRepository;
import com.hodol.han.samples.backend.shop.security.JwtTokenProvider;
import com.hodol.han.samples.backend.shop.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final UserService userService;
  private final JwtTokenProvider jwtTokenProvider;
  private final AuthenticationManager authenticationManager;
  private final UserRepository userRepository;

  public AuthController(
      UserService userService,
      JwtTokenProvider jwtTokenProvider,
      AuthenticationManager authenticationManager,
      UserRepository userRepository) {
    this.userService = userService;
    this.jwtTokenProvider = jwtTokenProvider;
    this.authenticationManager = authenticationManager;
    this.userRepository = userRepository;
  }

  @PostMapping("/signup")
  public ResponseEntity<?> signup(@Valid @RequestBody UserSignupRequest request) {
    userService.signup(request);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@Valid @RequestBody UserLoginRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
    User user =
        userRepository
            .findByUsername(request.getUsername())
            .orElseThrow(
                () ->
                    new org.springframework.security.authentication.BadCredentialsException(
                        "Invalid username or password"));
    String token = jwtTokenProvider.createToken(user.getUsername(), user.getRoles());
    return ResponseEntity.ok().body(token);
  }
}
