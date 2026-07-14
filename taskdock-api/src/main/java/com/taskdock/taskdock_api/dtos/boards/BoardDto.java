package com.taskdock.taskdock_api.dtos.boards;

import com.taskdock.taskdock_api.enums.BoardColor;
import java.time.Instant;

public record BoardDto(
    Long id,
    String name,
    String description,
    BoardColor color,
    boolean starred,
    boolean deleted,
    Long ownerId,
    Instant createdAt,
    Instant updatedAt) {}
