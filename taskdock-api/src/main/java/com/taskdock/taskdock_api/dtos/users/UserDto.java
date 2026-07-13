package com.taskdock.taskdock_api.dtos.users;

import java.time.Instant;

public record UserDto(
    String fullName,
    Integer age,
    String email,
    String phoneNumber,
    String profileImageUrl,
    Instant lastLoginAt,
    Instant createdAt,
    Instant updatedAt,
    Boolean emailVerified,
    Boolean phoneVerified) {}
