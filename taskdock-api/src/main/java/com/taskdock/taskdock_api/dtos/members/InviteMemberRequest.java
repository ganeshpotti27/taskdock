package com.taskdock.taskdock_api.dtos.members;

import com.taskdock.taskdock_api.enums.BoardRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InviteMemberRequest(
    @NotBlank(message = "Email is required.") @Email(message = "Invalid email.") String email,
    @NotNull(message = "Role is required.") BoardRole role) {}
