package com.taskdock.taskdock_api.enums;

import lombok.Getter;

@Getter
public enum BoardColor {
  BLUE("#3B82F6"),
  GREEN("#22C55E"),
  RED("#EF4444"),
  ORANGE("#F97316"),
  PURPLE("#8B5CF6"),
  PINK("#EC4899"),
  YELLOW("#EAB308"),
  GRAY("#6B7280");

  private final String hexCode;

  BoardColor(String hexCode) {
    this.hexCode = hexCode;
  }
}
