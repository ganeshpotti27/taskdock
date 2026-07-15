package com.taskdock.taskdock_api.dtos.boardlists;

import jakarta.validation.constraints.Size;

public record UpdateBoardListRequest(@Size(min = 2, max = 100) String name) {}
