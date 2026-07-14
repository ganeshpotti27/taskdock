package com.taskdock.taskdock_api.dtos.boards;

import com.taskdock.taskdock_api.enums.BoardColor;
import java.time.Instant;

public record BoardResponse(
    Long id,
    String name,
    String description,
    BoardColor color,
    boolean starred,
    Instant createdAt,
    Instant updatedAt) {}
