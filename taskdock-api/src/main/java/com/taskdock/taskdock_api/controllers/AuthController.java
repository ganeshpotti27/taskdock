package com.taskdock.taskdock_api.controllers;

import com.taskdock.taskdock_api.dtos.auth.AuthResponse;
import com.taskdock.taskdock_api.dtos.auth.LoginRequest;
import com.taskdock.taskdock_api.dtos.auth.RegisterRequest;
import com.taskdock.taskdock_api.services.AuthService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AuthController {

  AuthService authService;

  @PostMapping("/register")
  public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody RegisterRequest request) {
    return ResponseEntity.ok(authService.registerUser(request));
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> loginUser(@Valid @RequestBody LoginRequest request) {
    return ResponseEntity.ok(authService.loginUser(request));
  }
}
