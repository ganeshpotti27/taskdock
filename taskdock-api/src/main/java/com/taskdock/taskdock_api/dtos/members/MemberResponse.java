package com.taskdock.taskdock_api.dtos.members;

import com.taskdock.taskdock_api.enums.BoardRole;

public record MemberResponse(
    Long memberId,
    Long userId,
    String email,
    String fullName,
    BoardRole role,
    String profileImageUrl) {}
