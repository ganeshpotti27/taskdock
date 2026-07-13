package com.taskdock.taskdock_api.dtos.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LoginRequest(
    @NotBlank(message = "Email is required") @Email(message = "Invalid email") String email,
    @NotBlank(message = "New password is required")
        @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,16}$",
            message =
                "Password must be 8 to 16 characters long and contain at least one uppercase letter, one lowercase letter, one digit, and one special character.")
        String password) {}
