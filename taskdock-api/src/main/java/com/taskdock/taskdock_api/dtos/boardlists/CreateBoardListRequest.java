package com.taskdock.taskdock_api.dtos.boardlists;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateBoardListRequest(@NotBlank @Size(min = 2, max = 100) String name) {}
