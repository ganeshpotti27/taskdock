package com.taskdock.taskdock_api.dtos.boards;

import com.taskdock.taskdock_api.dtos.members.MemberListResponse;
import java.util.List;

public record BoardViewResponse(
    BoardResponse board, MemberListResponse members, List<BoardListWithTasksResponse> lists) {}
