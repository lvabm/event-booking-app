package com.eventbooking.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.exp-minutes}")
  private int expirationMinutes;

  private SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  }

  /**
   * Validate JWT token
   * @param token JWT token string
   * @return Claims if valid
   * @throws ExpiredJwtException if token is expired
   * @throws MalformedJwtException if token is invalid
   * @throws SignatureException if token signature is invalid
   */
  public Claims validateToken(String token) {
    try {
      return Jwts.parser()
          .verifyWith(getSigningKey())
          .build()
          .parseSignedClaims(token)
          .getPayload();
    } catch (ExpiredJwtException e) {
      throw e; // Re-throw để filter có thể bắt
    } catch (MalformedJwtException | SignatureException | IllegalArgumentException e) {
      throw new MalformedJwtException("Invalid token", e);
    }
  }

  /**
   * Extract user ID from token
   * @param token JWT token
   * @return User ID
   */
  public Long getUserIdFromToken(String token) {
    Claims claims = validateToken(token);
    return claims.get("userId", Long.class);
  }

  /**
   * Extract email from token
   * @param token JWT token
   * @return Email
   */
  public String getEmailFromToken(String token) {
    Claims claims = validateToken(token);
    return claims.getSubject();
  }

  /**
   * Check if token is expired
   * @param token JWT token
   * @return true if expired
   */
  public boolean isTokenExpired(String token) {
    try {
      Claims claims = validateToken(token);
      return claims.getExpiration().before(new Date());
    } catch (Exception e) {
      return true;
    }
  }

  /**
   * Generate JWT token for user
   * @param userId User ID
   * @param email User email
   * @return JWT token string
   */
  public String generateToken(Long userId, String email) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + expirationMinutes * 60 * 1000L);

    return Jwts.builder()
        .subject(email)
        .claim("userId", userId)
        .issuedAt(now)
        .expiration(expiryDate)
        .signWith(getSigningKey())
        .compact();
  }
}

