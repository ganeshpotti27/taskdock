package com.taskdock.taskdock_api.services;

import com.taskdock.taskdock_api.dtos.boardlists.BoardListResponse;
import com.taskdock.taskdock_api.dtos.boardlists.BoardListsResponse;
import com.taskdock.taskdock_api.dtos.boardlists.CreateBoardListRequest;
import com.taskdock.taskdock_api.dtos.boardlists.ReorderBoardListsRequest;
import com.taskdock.taskdock_api.dtos.boardlists.UpdateBoardListRequest;

public interface BoardListService {

  BoardListResponse createList(Long boardId, CreateBoardListRequest request);

  BoardListResponse updateList(Long boardId, Long listId, UpdateBoardListRequest request);

  BoardListsResponse getActiveLists(Long boardId);

  BoardListsResponse getArchivedLists(Long boardId);

  void reorderLists(Long boardId, ReorderBoardListsRequest request);

  void archiveList(Long boardId, Long listId);

  void restoreList(Long boardId, Long listId);

  void deleteArchivedList(Long boardId, Long listId);
}
