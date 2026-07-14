package com.taskdock.taskdock_api.security;

import static com.taskdock.taskdock_api.enums.BoardPermission.*;

import com.taskdock.taskdock_api.entities.Board;
import com.taskdock.taskdock_api.entities.User;
import com.taskdock.taskdock_api.enums.BoardPermission;
import com.taskdock.taskdock_api.repositories.BoardMemberRepository;
import com.taskdock.taskdock_api.repositories.BoardRepository;
import com.taskdock.taskdock_api.repositories.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("security")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SecurityExpressions {

  BoardRepository boardRepository;
  BoardMemberRepository boardMemberRepository;
  UserRepository userRepository;

  public boolean canViewBoard(Long boardId) {
    return hasPermission(boardId, BOARD_VIEW);
  }

  public boolean canEditBoard(Long boardId) {
    return hasPermission(boardId, BOARD_EDIT);
  }

  public boolean canDeleteBoard(Long boardId) {
    return hasPermission(boardId, BOARD_DELETE);
  }

  public boolean canViewMembers(Long boardId) {
    return hasPermission(boardId, BOARD_MEMBERS_VIEW);
  }

  public boolean canManageMembers(Long boardId) {
    return hasPermission(boardId, BOARD_MEMBERS_MANAGE);
  }

  public boolean canCreateTask(Long boardId) {
    return hasPermission(boardId, TASK_CREATE);
  }

  public boolean canViewTask(Long boardId) {
    return hasPermission(boardId, TASK_VIEW);
  }

  public boolean canUpdateTask(Long boardId) {
    return hasPermission(boardId, TASK_UPDATE);
  }

  public boolean canDeleteTask(Long boardId) {
    return hasPermission(boardId, TASK_DELETE);
  }

  public boolean canMoveTask(Long boardId) {
    return hasPermission(boardId, TASK_MOVE);
  }

  private boolean hasPermission(Long boardId, BoardPermission permission) {

    String email = SecurityContextHolder.getContext().getAuthentication().getName();

    User currentUser = userRepository.findByEmail(email).orElse(null);

    if (currentUser == null) {
      return false;
    }

    Board board = boardRepository.findById(boardId).orElse(null);

    if (board == null) {
      return false;
    }

    // Owner always has every permission
    if (board.getOwner().getId().equals(currentUser.getId())) {
      return true;
    }

    return boardMemberRepository
        .findByBoardAndUser(board, currentUser)
        .map(member -> member.getRole().hasPermission(permission))
        .orElse(false);
  }
}
