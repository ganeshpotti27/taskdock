package com.taskdock.taskdock_api.services.impl;

import com.taskdock.taskdock_api.dtos.auth.AuthResponse;
import com.taskdock.taskdock_api.dtos.auth.LoginRequest;
import com.taskdock.taskdock_api.dtos.auth.RegisterRequest;
import com.taskdock.taskdock_api.entities.User;
import com.taskdock.taskdock_api.exceptions.BadRequestException;
import com.taskdock.taskdock_api.mappers.UserMapper;
import com.taskdock.taskdock_api.repositories.UserRepository;
import com.taskdock.taskdock_api.services.AuthService;
import com.taskdock.taskdock_api.services.JwtService;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AuthServiceImpl implements AuthService {

  UserRepository userRepository;
  UserMapper userMapper;
  PasswordEncoder passwordEncoder;
  JwtService jwtService;
  AuthenticationManager authenticationManager;

  @Override
  public AuthResponse registerUser(RegisterRequest request) {

    if (userRepository.existsByEmail(request.email())) {
      throw new BadRequestException("Email already exists.");
    }

    if (userRepository.existsByPhoneNumber(request.phoneNumber())) {
      throw new BadRequestException("Phone number already exists.");
    }

    User user = userMapper.toEntity(request);

    user.setPasswordHash(passwordEncoder.encode(request.password()));

    user.setLastLoginAt(Instant.now());

    user = userRepository.save(user);

    String accessToken = jwtService.generateToken(user);

    return new AuthResponse(
        accessToken,
        "Bearer",
        jwtService.extractExpiration(accessToken),
        userMapper.toUserResponse(user));
  }

  @Override
  public AuthResponse loginUser(LoginRequest request) {

    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.email(), request.password()));

    User user =
        userRepository
            .findByEmail(request.email())
            .orElseThrow(
                () -> new BadRequestException("User not found with email: " + request.email()));

    user.setLastLoginAt(Instant.now());

    userRepository.save(user);

    String accessToken = jwtService.generateToken(user);

    return new AuthResponse(
        accessToken,
        "Bearer",
        jwtService.extractExpiration(accessToken),
        userMapper.toUserResponse(user));
  }
}
