package com.taskdock.taskdock_api.controllers;

import com.taskdock.taskdock_api.dtos.tasks.*;
import com.taskdock.taskdock_api.services.TaskService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/boards/{boardId}")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TaskController {

  TaskService taskService;

  @PostMapping("/tasks")
  public ResponseEntity<TaskResponse> createTask(
      @PathVariable Long boardId, @Valid @RequestBody CreateTaskRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(boardId, request));
  }

  @GetMapping("/tasks/{taskId}")
  public ResponseEntity<TaskResponse> getTask(
      @PathVariable Long boardId, @PathVariable Long taskId) {
    return ResponseEntity.ok(taskService.getTask(boardId, taskId));
  }

  @GetMapping("/lists/{listId}/tasks")
  public ResponseEntity<TaskListResponse> getTasksByBoardList(
      @PathVariable Long boardId, @PathVariable Long listId) {
    return ResponseEntity.ok(taskService.getTasksByBoardList(boardId, listId));
  }

  @PatchMapping("/tasks/{taskId}")
  public ResponseEntity<TaskResponse> updateTask(
      @PathVariable Long boardId,
      @PathVariable Long taskId,
      @Valid @RequestBody UpdateTaskRequest request) {
    return ResponseEntity.ok(taskService.updateTask(boardId, taskId, request));
  }

  @PatchMapping("/tasks/{taskId}/move")
  public ResponseEntity<Void> moveTask(
      @PathVariable long boardId,
      @PathVariable Long taskId,
      @Valid @RequestBody MoveTaskRequest request) {
    taskService.moveTask(boardId, taskId, request);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/tasks/{taskId}")
  public ResponseEntity<Void> deleteTask(@PathVariable Long boardId, @PathVariable Long taskId) {
    taskService.deleteTask(boardId, taskId);
    return ResponseEntity.noContent().build();
  }
}
