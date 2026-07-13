package com.taskdock.taskdock_api.services;

import com.taskdock.taskdock_api.dtos.auth.AuthResponse;
import com.taskdock.taskdock_api.dtos.auth.LoginRequest;
import com.taskdock.taskdock_api.dtos.auth.RegisterRequest;

public interface AuthService {

  AuthResponse registerUser(RegisterRequest request);

  AuthResponse loginUser(LoginRequest request);
}
