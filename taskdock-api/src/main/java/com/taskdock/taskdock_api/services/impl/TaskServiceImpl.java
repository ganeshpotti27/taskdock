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

    BoardList boardList = getActiveBoardList(board, request.boardListId());

    Task task = taskMapper.toEntity(request);

    task.setBoardList(boardList);

    task.setCreatedBy(jwtAuthUtil.getCurrentUser());

    task.setAssignee(resolveAssignee(board, request.assigneeId()));

    task.setPriority(request.priority() != null ? request.priority() : TaskPriority.MEDIUM);

    task.setPosition(getNextTaskPosition(boardList));

    return taskMapper.toTaskResponse(taskRepository.save(task));
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
    return taskMapper.toTaskResponse(taskRepository.save(task));
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

    BoardList destinationList = getActiveBoardList(board, request.destinationListId());

    if (sourceList.getId().equals(destinationList.getId())) {
      return;
    }

    int oldPosition = task.getPosition();

    // Remove gap from source list
    compactTaskPositions(sourceList, oldPosition);

    task.setBoardList(destinationList);
    task.setPosition(getNextTaskPosition(destinationList));

    taskRepository.save(task);
  }

  private User resolveAssignee(Board board, Long assigneeId) {
    if (assigneeId == null) {
      return board.getOwner();
    }

    return validateAssignee(board, assigneeId);
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

  private int getNextTaskPosition(BoardList boardList) {

    return java.util.Optional.ofNullable(taskRepository.findMaxPosition(boardList))
        .map(position -> position + 1)
        .orElse(1);
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

  private BoardList getActiveBoardList(Board board, Long listId) {

    if (listId == null) {
      throw new BadRequestException("Board list is required.");
    }

    BoardList boardList =
        boardListRepository
            .findByIdAndBoard(listId, board)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Board list not found with id: ", listId.toString()));

    if (boardList.isArchived()) {
      throw new BadRequestException("Board list is archived.");
    }

    return boardList;
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
