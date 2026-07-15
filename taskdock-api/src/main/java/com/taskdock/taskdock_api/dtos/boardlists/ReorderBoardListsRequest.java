package com.taskdock.taskdock_api.dtos.boardlists;

import java.util.List;

public record ReorderBoardListsRequest(List<ReorderBoardListRequest> lists) {}
