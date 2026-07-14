package com.taskdock.taskdock_api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BoardPermission {
  BOARD_VIEW("board:view"),
  BOARD_EDIT("board:edit"),
  BOARD_DELETE("board:delete"),

  BOARD_MEMBERS_VIEW("board_members:view"),
  BOARD_MEMBERS_MANAGE("board_members:manage"),

  TASK_CREATE("task:create"),
  TASK_VIEW("task:view"),
  TASK_UPDATE("task:update"),
  TASK_DELETE("task:delete"),
  TASK_MOVE("task:move");

  private final String value;
}
