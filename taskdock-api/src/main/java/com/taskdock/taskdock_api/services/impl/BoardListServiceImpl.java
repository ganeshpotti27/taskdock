package com.taskdock.taskdock_api.services.impl;

import com.taskdock.taskdock_api.dtos.boardlists.*;
import com.taskdock.taskdock_api.entities.Board;
import com.taskdock.taskdock_api.entities.BoardList;
import com.taskdock.taskdock_api.exceptions.BadRequestException;
import com.taskdock.taskdock_api.exceptions.ResourceNotFoundException;
import com.taskdock.taskdock_api.mappers.BoardListMapper;
import com.taskdock.taskdock_api.repositories.BoardListRepository;
import com.taskdock.taskdock_api.repositories.BoardRepository;
import com.taskdock.taskdock_api.services.BoardListService;
import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BoardListServiceImpl implements BoardListService {

  BoardRepository boardRepository;
  BoardListRepository boardListRepository;
  BoardListMapper boardListMapper;

  @Override
  @PreAuthorize("@security.canEditBoard(#boardId)")
  public BoardListResponse createList(Long boardId, CreateBoardListRequest request) {

    Board board = getBoard(boardId);

    if (boardListRepository.countByBoard(board) >= 15) {
      throw new BadRequestException("Maximum 15 lists are allowed.");
    }

    if (boardListRepository.countByBoardAndArchivedFalse(board) >= 7) {
      throw new BadRequestException("Maximum 7 active lists are allowed.");
    }

    if (boardListRepository.existsByBoardAndNameIgnoreCaseAndArchivedFalse(board, request.name())) {

      throw new BadRequestException("List with the same name already exists.");
    }

    BoardList list = boardListMapper.toEntity(request);

    list.setBoard(board);
    list.setArchived(false);

    Integer maxPosition = boardListRepository.findMaxPositionByBoard(board);

    list.setPosition(maxPosition + 1);

    list = boardListRepository.save(list);

    return boardListMapper.toBoardListResponse(list);
  }

  @Override
  @PreAuthorize("@security.canEditBoard(#boardId)")
  public BoardListResponse updateList(Long boardId, Long listId, UpdateBoardListRequest request) {

    Board board = getBoard(boardId);

    BoardList list = getBoardList(board, listId);

    if (request.name() != null
        && boardListRepository.existsByBoardAndNameIgnoreCaseAndArchivedFalseAndIdNot(
            board, request.name(), listId)) {

      throw new BadRequestException("List with the same name already exists.");
    }

    boardListMapper.updateBoardListFromRequest(request, list);

    list = boardListRepository.save(list);

    return boardListMapper.toBoardListResponse(list);
  }

  @Override
  @PreAuthorize("@security.canViewBoard(#boardId)")
  public BoardListsResponse getActiveLists(Long boardId) {

    Board board = getBoard(boardId);

    List<BoardList> lists =
        boardListRepository.findAllByBoardAndArchivedFalseOrderByPositionAsc(board);

    List<BoardListResponse> responses = boardListMapper.toBoardListResponses(lists);

    long totalLists = boardListRepository.countByBoard(board);
    long activeLists = boardListRepository.countByBoardAndArchivedFalse(board);

    return new BoardListsResponse(
        responses,
        (int) totalLists,
        (int) activeLists,
        (int) (totalLists - activeLists),
        15,
        7,
        totalLists < 15);
  }

  @Override
  @PreAuthorize("@security.canViewBoard(#boardId)")
  public BoardListsResponse getArchivedLists(Long boardId) {

    Board board = getBoard(boardId);

    List<BoardList> lists =
        boardListRepository.findAllByBoardAndArchivedTrueOrderByPositionAsc(board);

    List<BoardListResponse> responses = boardListMapper.toBoardListResponses(lists);

    long totalLists = boardListRepository.countByBoard(board);
    long activeLists = boardListRepository.countByBoardAndArchivedFalse(board);

    return new BoardListsResponse(
        responses,
        (int) totalLists,
        (int) activeLists,
        (int) (totalLists - activeLists),
        15,
        7,
        totalLists < 15);
  }

  @Override
  @PreAuthorize("@security.canEditBoard(#boardId)")
  @Transactional
  public void reorderLists(Long boardId, ReorderBoardListsRequest request) {

    Board board = getBoard(boardId);

    List<BoardList> activeLists =
        boardListRepository.findAllByBoardAndArchivedFalseOrderByPositionAsc(board);

    if (request.lists().size() != activeLists.size()) {
      throw new BadRequestException("All active lists must be reordered.");
    }

    Map<Long, BoardList> listMap =
        activeLists.stream().collect(Collectors.toMap(BoardList::getId, Function.identity()));

    Set<Long> listIds = new HashSet<>();
    Set<Integer> positions = new HashSet<>();

    // ======================================================
    // Validate request
    // ======================================================

    for (ReorderBoardListRequest item : request.lists()) {

      if (!listIds.add(item.listId())) {
        throw new BadRequestException("Duplicate list id: " + item.listId());
      }

      if (!positions.add(item.newPosition())) {
        throw new BadRequestException("Duplicate position: " + item.newPosition());
      }

      if (item.newPosition() < 1 || item.newPosition() > activeLists.size()) {
        throw new BadRequestException("Invalid position: " + item.newPosition());
      }

      if (!listMap.containsKey(item.listId())) {
        throw new BadRequestException("Invalid list id: " + item.listId());
      }
    }

    // ======================================================
    // Phase 1:
    // Move all lists to temporary negative positions
    // to avoid unique constraint conflicts.
    // ======================================================

    for (BoardList list : activeLists) {
      list.setPosition(-list.getPosition());
    }

    boardListRepository.saveAll(activeLists);
    boardListRepository.flush();

    // ======================================================
    // Phase 2:
    // Assign requested positions.
    // ======================================================

    for (ReorderBoardListRequest item : request.lists()) {

      BoardList list = listMap.get(item.listId());

      list.setPosition(item.newPosition());
    }

    boardListRepository.saveAll(activeLists);
  }

  @Override
  @PreAuthorize("@security.canEditBoard(#boardId)")
  public void archiveList(Long boardId, Long listId) {

    Board board = getBoard(boardId);

    BoardList list = getBoardList(board, listId);

    if (list.isArchived()) {
      return;
    }

    list.setArchived(true);

    boardListRepository.save(list);
  }

  @Override
  @PreAuthorize("@security.canEditBoard(#boardId)")
  public void restoreList(Long boardId, Long listId) {

    Board board = getBoard(boardId);

    BoardList list = getBoardList(board, listId);

    if (boardListRepository.countByBoardAndArchivedFalse(board) >= 7) {
      throw new BadRequestException("Maximum 7 active lists are allowed.");
    }

    list.setArchived(false);

    boardListRepository.save(list);
  }

  @Override
  @PreAuthorize("@security.canEditBoard(#boardId)")
  public void deleteArchivedList(Long boardId, Long listId) {

    Board board = getBoard(boardId);

    BoardList list = getBoardList(board, listId);

    if (!list.isArchived()) {
      throw new BadRequestException("Only archived lists can be permanently deleted.");
    }

    boardListRepository.delete(list);
  }

  // =====================================================
  // Helper Methods
  // =====================================================

  private Board getBoard(Long boardId) {

    return boardRepository
        .findByIdAndDeletedFalse(boardId)
        .orElseThrow(
            () -> new ResourceNotFoundException("Board not found with id: ", boardId.toString()));
  }

  private BoardList getBoardList(Board board, Long listId) {

    return boardListRepository
        .findByIdAndBoard(listId, board)
        .orElseThrow(
            () -> new ResourceNotFoundException("List not found with id: ", listId.toString()));
  }
}
