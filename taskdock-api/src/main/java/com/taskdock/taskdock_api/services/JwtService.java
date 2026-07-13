package com.taskdock.taskdock_api.services;

import java.time.Instant;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {

  String generateToken(UserDetails userDetails);

  String extractUsername(String token);

  Instant extractExpiration(String token);

  boolean isTokenValid(String token, UserDetails userDetails);
}
