package com.hodol.han.samples.backend.shop.dto;

public class JwtResponse {
  private String tokenType;
  private String accessToken;

  public JwtResponse(String tokenType, String accessToken) {
    this.tokenType = tokenType;
    this.accessToken = accessToken;
  }

  public String getTokenType() {
    return tokenType;
  }

  public void setTokenType(String tokenType) {
    this.tokenType = tokenType;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }
}
