package com.taskdock.taskdock_api.dtos.tasks;

import jakarta.validation.constraints.NotNull;

public record MoveTaskRequest(@NotNull Long destinationListId) {}
