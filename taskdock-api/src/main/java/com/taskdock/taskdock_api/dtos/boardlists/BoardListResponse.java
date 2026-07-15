package com.taskdock.taskdock_api.dtos.boardlists;

import java.time.Instant;

public record BoardListResponse(
    Long id,
    String name,
    Integer position,
    Boolean archived,
    Instant createdAt,
    Instant updatedAt) {}
