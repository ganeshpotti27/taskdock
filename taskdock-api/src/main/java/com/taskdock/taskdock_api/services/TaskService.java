package com.taskdock.taskdock_api.services;

import com.taskdock.taskdock_api.dtos.tasks.*;

public interface TaskService {
  TaskResponse createTask(Long boardId, CreateTaskRequest request);

  TaskResponse getTask(Long boardId, Long taskId);

  TaskListResponse getTasksByBoardList(Long boardId, Long listId);

  TaskResponse updateTask(Long boardId, Long taskId, UpdateTaskRequest request);

  void deleteTask(Long boardId, Long taskId);

  void moveTask(Long boardId, Long taskId, MoveTaskRequest request);
}
