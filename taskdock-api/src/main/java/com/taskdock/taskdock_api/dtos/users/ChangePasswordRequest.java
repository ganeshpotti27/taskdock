package com.taskdock.taskdock_api.dtos.users;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ChangePasswordRequest(
    @NotBlank(message = "Old Password is required")
        @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,16}$",
            message =
                "Old Password must be 8 to 16 characters long and contain at least one uppercase letter, one lowercase letter, one digit, and one special character.")
        String oldPassword,
    @NotBlank(message = "New Password is required")
        @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,16}$",
            message =
                "New Password must be 8 to 16 characters long and contain at least one uppercase letter, one lowercase letter, one digit, and one special character.")
        String newPassword) {}
