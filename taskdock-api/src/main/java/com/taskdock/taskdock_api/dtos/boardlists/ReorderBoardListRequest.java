package com.taskdock.taskdock_api.dtos.boardlists;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ReorderBoardListRequest(@NotNull Long listId, @NotNull @Min(1) Integer newPosition) {}
