package com.hodol.han.samples.backend.shop.dto;

import com.hodol.han.samples.backend.shop.validation.Trim;
import jakarta.validation.constraints.NotBlank;

public class UserSignupRequest {
  @Trim @NotBlank private String username;
  @NotBlank private String password;

  // Getters and Setters
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
