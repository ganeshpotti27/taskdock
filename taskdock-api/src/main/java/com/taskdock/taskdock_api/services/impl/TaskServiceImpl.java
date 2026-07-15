package com.taskdock.taskdock_api.services.impl;

import com.taskdock.taskdock_api.dtos.tasks.*;
import com.taskdock.taskdock_api.entities.*;
import com.taskdock.taskdock_api.enums.BoardRole;
import com.taskdock.taskdock_api.enums.TaskPriority;
import com.taskdock.taskdock_api.exceptions.BadRequestException;
import com.taskdock.taskdock_api.exceptions.ResourceNotFoundException;
import com.taskdock.taskdock_api.mappers.TaskMapper;
import com.taskdock.taskdock_api.repositories.*;
import com.taskdock.taskdock_api.security.JwtAuthUtil;
import com.taskdock.taskdock_api.services.TaskService;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TaskServiceImpl implements TaskService {

  TaskRepository taskRepository;
  BoardRepository boardRepository;
  BoardListRepository boardListRepository;
  BoardMemberRepository boardMemberRepository;
  UserRepository userRepository;

  TaskMapper taskMapper;

  JwtAuthUtil jwtAuthUtil;

  @Override
  @PreAuthorize("@security.canEditBoard(#boardId)")
  public TaskResponse createTask(Long boardId, CreateTaskRequest request) {

    Board board = getBoard(boardId);

    BoardList boardList =
        boardListRepository
            .findFirstByBoardAndArchivedFalseOrderByPositionAsc(board)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "No active board list found in board: ", boardId.toString()));

    Task task = taskMapper.toEntity(request);

    task.setBoardList(boardList);

    task.setCreatedBy(jwtAuthUtil.getCurrentUser());

    if (request.assigneeId() != null) {
      task.setAssignee(validateAssignee(board, request.assigneeId()));
    } else {
      task.setAssignee(board.getOwner());
    }

    if (request.priority() != null) {
      task.setPriority(request.priority());
    } else {
      task.setPriority(TaskPriority.MEDIUM);
    }

    Integer maxPosition = taskRepository.findMaxPosition(boardList);

    task.setPosition(maxPosition + 1);

    task = taskRepository.save(task);

    return taskMapper.toTaskResponse(task);
  }

  @Override
  @PreAuthorize("@security.canViewBoard(#boardId)")
  public TaskResponse getTask(Long boardId, Long taskId) {

    return taskMapper.toTaskResponse(getTaskEntity(boardId, taskId));
  }

  @Override
  @PreAuthorize("@security.canViewBoard(#boardId)")
  public TaskListResponse getTasksByBoardList(Long boardId, Long listId) {

    Board board = getBoard(boardId);

    BoardList boardList =
        boardListRepository
            .findByIdAndBoard(listId, board)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Board list not found with id: ", listId.toString()));

    List<Task> tasks = taskRepository.findAllByBoardListOrderByPositionAsc(boardList);

    List<TaskResponse> responses = taskMapper.toTaskResponses(tasks);

    return new TaskListResponse(responses, responses.size());
  }

  @Override
  @PreAuthorize("@security.canEditBoard(#boardId)")
  public TaskResponse updateTask(Long boardId, Long taskId, UpdateTaskRequest request) {

    Task task = getTaskEntity(boardId, taskId);

    taskMapper.updateTaskFromRequest(request, task);

    Board board = getBoard(boardId);

    if (request.assigneeId() != null) {
      task.setAssignee(validateAssignee(board, request.assigneeId()));
    }

    task = taskRepository.save(task);

    return taskMapper.toTaskResponse(task);
  }

  @Override
  @PreAuthorize("@security.canEditBoard(#boardId)")
  public void deleteTask(Long boardId, Long taskId) {

    Task task = getTaskEntity(boardId, taskId);

    BoardList boardList = task.getBoardList();

    int deletedPosition = task.getPosition();

    taskRepository.delete(task);

    compactTaskPositions(boardList, deletedPosition);
  }

  @Override
  @PreAuthorize("@security.canEditBoard(#boardId)")
  public void moveTask(Long boardId, Long taskId, MoveTaskRequest request) {

    Board board = getBoard(boardId);

    Task task = getTaskEntity(boardId, taskId);

    BoardList sourceList = task.getBoardList();

    BoardList destinationList =
        boardListRepository
            .findByIdAndBoard(request.destinationListId(), board)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Board list not found with id: ", request.destinationListId().toString()));

    if (sourceList.getId().equals(destinationList.getId())) {
      return;
    }

    if (destinationList.isArchived()) {
      throw new BadRequestException("Tasks cannot be moved to an archived list.");
    }

    int oldPosition = task.getPosition();

    // Remove gap from source list
    compactTaskPositions(sourceList, oldPosition);

    // Append to destination list
    Integer maxPosition = taskRepository.findMaxPosition(destinationList);

    task.setBoardList(destinationList);
    task.setPosition(maxPosition + 1);

    taskRepository.save(task);
  }

  private Board getBoard(Long boardId) {

    return boardRepository
        .findByIdAndDeletedFalse(boardId)
        .orElseThrow(
            () -> new ResourceNotFoundException("Board not found with id: ", boardId.toString()));
  }

  private Task getTaskEntity(Long boardId, Long taskId) {

    return taskRepository
        .findByIdAndBoardListBoardId(taskId, boardId)
        .orElseThrow(
            () -> new ResourceNotFoundException("Task not found with id: ", taskId.toString()));
  }

  private User getUser(Long userId) {

    return userRepository
        .findById(userId)
        .orElseThrow(
            () -> new ResourceNotFoundException("User not found with id: ", userId.toString()));
  }

  /** Closes the position gap after a task is deleted or moved. */
  private void compactTaskPositions(BoardList boardList, int deletedPosition) {

    List<Task> tasks =
        taskRepository.findAllByBoardListAndPositionGreaterThanOrderByPositionAsc(
            boardList, deletedPosition);

    for (Task task : tasks) {
      task.setPosition(task.getPosition() - 1);
    }

    taskRepository.saveAll(tasks);
  }

  private User validateAssignee(Board board, Long assigneeId) {

    User assignee = getUser(assigneeId);

    // Board owner can always be assigned
    if (board.getOwner().getId().equals(assignee.getId())) {
      return assignee;
    }

    BoardMember member =
        boardMemberRepository
            .findByBoardAndUser(board, assignee)
            .orElseThrow(() -> new BadRequestException("User is not a member of this board."));

    if (member.getRole() == BoardRole.VIEWER) {
      throw new BadRequestException("Viewer cannot be assigned tasks.");
    }

    return assignee;
  }
}
