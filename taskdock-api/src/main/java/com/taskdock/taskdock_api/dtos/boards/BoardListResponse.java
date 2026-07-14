package com.taskdock.taskdock_api.dtos.boards;

import java.util.List;

public record BoardListResponse(
    List<BoardResponse> boards, int totalBoards, int maxBoards, boolean canCreateBoard) {}
