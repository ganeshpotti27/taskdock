package com.taskdock.taskdock_api.enums;

import static com.taskdock.taskdock_api.enums.BoardPermission.*;

import java.util.Set;
import lombok.Getter;

@Getter
public enum BoardRole {
  OWNER(
      BOARD_VIEW,
      BOARD_EDIT,
      BOARD_DELETE,
      BOARD_MEMBERS_VIEW,
      BOARD_MEMBERS_MANAGE,
      TASK_CREATE,
      TASK_VIEW,
      TASK_UPDATE,
      TASK_DELETE,
      TASK_MOVE),

  EDITOR(
      BOARD_VIEW, BOARD_MEMBERS_VIEW, TASK_CREATE, TASK_VIEW, TASK_UPDATE, TASK_DELETE, TASK_MOVE),

  VIEWER(BOARD_VIEW, BOARD_MEMBERS_VIEW, TASK_VIEW);

  private final Set<BoardPermission> permissions;

  BoardRole(BoardPermission... permissions) {
    this.permissions = Set.of(permissions);
  }

  public boolean hasPermission(BoardPermission permission) {
    return permissions.contains(permission);
  }
}
