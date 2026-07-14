package com.taskdock.taskdock_api.dtos.boards;

import com.taskdock.taskdock_api.enums.BoardColor;
import jakarta.validation.constraints.Size;

public record UpdateBoardRequest(
    @Size(min = 2, max = 100, message = "Board name must be between 2 and 100 characters.")
        String name,
    @Size(max = 500, message = "Description cannot exceed 500 characters.") String description,
    BoardColor color,
    Boolean starred) {}
