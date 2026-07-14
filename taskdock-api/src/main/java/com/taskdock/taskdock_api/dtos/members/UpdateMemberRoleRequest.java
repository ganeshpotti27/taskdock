package com.taskdock.taskdock_api.dtos.members;

import com.taskdock.taskdock_api.enums.BoardRole;
import jakarta.validation.constraints.NotNull;

public record UpdateMemberRoleRequest(@NotNull(message = "Role is required.") BoardRole role) {}
