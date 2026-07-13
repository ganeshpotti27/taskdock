package com.taskdock.taskdock_api.dtos.users;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record UpdateUserProfileRequest(
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters.")
        String fullName,
    @Min(value = 13, message = "Age must be at least 13 years.")
        @Max(value = 120, message = "Age must not exceed 120 years.")
        Integer age) {}
