package com.taskdock.taskdock_api.services.impl;

import com.taskdock.taskdock_api.services.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

  @Value("${jwt.secret-key}")
  private String jwtSecretKey;

  @Value("${jwt.expiration}")
  private long jwtExpiration;

  private SecretKey getSecretKey() {
    return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
  }

  @Override
  public String generateToken(UserDetails userDetails) {

    Date now = new Date();

    Date expiry = new Date(now.getTime() + jwtExpiration);

    return Jwts.builder()
        .subject(userDetails.getUsername())
        .issuedAt(now)
        .expiration(expiry)
        .signWith(getSecretKey())
        .compact();
  }

  @Override
  public String extractUsername(String token) {
    return extractClaims(token).getSubject();
  }

  @Override
  public Instant extractExpiration(String token) {
    return extractClaims(token).getExpiration().toInstant();
  }

  @Override
  public boolean isTokenValid(String token, UserDetails userDetails) {
    return extractUsername(token).equals(userDetails.getUsername())
        && extractExpiration(token).isAfter(Instant.now());
  }

  private Claims extractClaims(String token) {
    return Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(token).getPayload();
  }
}
