package com.hodol.han.samples.backend.shop.controller;

import com.hodol.han.samples.backend.shop.dto.UserLoginRequest;
import com.hodol.han.samples.backend.shop.dto.UserSignupRequest;
import com.hodol.han.samples.backend.shop.security.JwtTokenProvider;
import com.hodol.han.samples.backend.shop.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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

  public AuthController(
      UserService userService,
      JwtTokenProvider jwtTokenProvider,
      AuthenticationManager authenticationManager) {
    this.userService = userService;
    this.jwtTokenProvider = jwtTokenProvider;
    this.authenticationManager = authenticationManager;
  }

  @PostMapping("/signup")
  public ResponseEntity<Void> signup(@Valid @RequestBody UserSignupRequest request) {
    userService.signup(request);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/login")
  public ResponseEntity<String> login(@Valid @RequestBody UserLoginRequest request) {
    String username = request.getUsername();

    Authentication auth =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, request.getPassword()));

    String token = jwtTokenProvider.generateToken(auth);
    return ResponseEntity.ok(token);
  }
}
