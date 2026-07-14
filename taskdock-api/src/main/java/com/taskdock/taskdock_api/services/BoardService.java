package com.taskdock.taskdock_api.services;

import com.taskdock.taskdock_api.dtos.boards.BoardColorResponse;
import com.taskdock.taskdock_api.dtos.boards.BoardListResponse;
import com.taskdock.taskdock_api.dtos.boards.BoardResponse;
import com.taskdock.taskdock_api.dtos.boards.CreateBoardRequest;
import com.taskdock.taskdock_api.dtos.boards.UpdateBoardRequest;
import java.util.List;

public interface BoardService {

  /** Creates a new board for the authenticated user. (Maximum 3 active boards for free users.) */
  BoardResponse createBoard(CreateBoardRequest request);

  /** Updates board details. */
  BoardResponse updateBoard(Long boardId, UpdateBoardRequest request);

  /** Returns a board if the current user has access. */
  BoardResponse getBoard(Long boardId);

  /** Returns all active boards owned by the current user. */
  BoardListResponse getAccessibleBoards();

  /** Soft deletes a board. */
  void softDeleteBoard(Long boardId);

  /** Returns all available board colors. */
  List<BoardColorResponse> getColors();
}
