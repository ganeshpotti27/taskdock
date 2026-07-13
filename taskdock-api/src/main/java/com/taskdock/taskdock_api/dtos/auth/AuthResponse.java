package com.taskdock.taskdock_api.dtos.auth;

import com.taskdock.taskdock_api.dtos.users.UserResponse;
import java.time.Instant;

public record AuthResponse(
    String accessToken, String tokenType, Instant expiresAt, UserResponse user) {}
