package com.hodol.han.samples.backend.shop.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {
  private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);
  private final Key key;

  public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
    if (secretKey == null || secretKey.isEmpty()) {
      throw new IllegalStateException("jwt.secret property is not set");
    }
    this.key = Keys.hmacShaKeyFor(secretKey.getBytes(java.nio.charset.StandardCharsets.UTF_8));
  }

  private final long validityInMilliseconds = 1000 * 60 * 60; // 1 hour

  public String createToken(String username, Set<String> roles) {
    Claims claims = Jwts.claims().setSubject(username);
    claims.put("roles", roles);
    Date now = new Date();
    Date validity = new Date(now.getTime() + validityInMilliseconds);
    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(validity)
        .signWith(key)
        .compact();
  }

  public String getUsername(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
      return true;
    } catch (io.jsonwebtoken.ExpiredJwtException
        | io.jsonwebtoken.MalformedJwtException
        | io.jsonwebtoken.security.SecurityException
        | IllegalArgumentException e) {
      logger.error("Token validation failed", e);
      return false;
    }
  }
}
