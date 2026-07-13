package com.taskdock.taskdock_api.dtos.users;

import java.time.Instant;

public record UserResponse(
    String fullName,
    String email,
    String phoneNumber,
    String profileImageUrl,
    Instant lastLoginAt) {}
