package com.taskdock.taskdock_api.dtos.tasks;

import com.taskdock.taskdock_api.enums.TaskPriority;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record CreateTaskRequest(
    @NotBlank(message = "Task title is required.") @Size(min = 2, max = 200) String title,
    @Size(max = 5000) String description,
    TaskPriority priority,
    @NotNull(message = "Due date is required.") @Future(message = "Due date must be in the future.")
        LocalDateTime dueDate,
    Long assigneeId,
    @NotNull(message = "Board list is required.") Long boardListId) {}
