package com.taskdock.taskdock_api.dtos.tasks;

import java.util.List;

public record TaskListResponse(List<TaskResponse> tasks, Integer totalTasks) {}
