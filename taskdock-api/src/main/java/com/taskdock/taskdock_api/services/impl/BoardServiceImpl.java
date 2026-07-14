package com.taskdock.taskdock_api.services.impl;

import com.taskdock.taskdock_api.dtos.boards.*;
import com.taskdock.taskdock_api.entities.Board;
import com.taskdock.taskdock_api.entities.User;
import com.taskdock.taskdock_api.enums.BoardColor;
import com.taskdock.taskdock_api.exceptions.BadRequestException;
import com.taskdock.taskdock_api.exceptions.ResourceNotFoundException;
import com.taskdock.taskdock_api.mappers.BoardMapper;
import com.taskdock.taskdock_api.repositories.BoardRepository;
import com.taskdock.taskdock_api.repositories.UserRepository;
import com.taskdock.taskdock_api.services.BoardService;
import java.util.Arrays;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BoardServiceImpl implements BoardService {

  BoardRepository boardRepository;
  UserRepository userRepository;
  BoardMapper boardMapper;

  @Override
  public BoardResponse createBoard(CreateBoardRequest request) {

    User currentUser = getCurrentUser();

    long ownedBoards = boardRepository.countByOwnerAndDeletedFalse(currentUser);

    if (ownedBoards >= 3) {
      throw new BadRequestException("Free plan allows a maximum of 3 boards.");
    }

    if (boardRepository.existsByOwnerAndNameIgnoreCaseAndDeletedFalse(
        currentUser, request.name())) {

      throw new BadRequestException("Board with the same name already exists.");
    }

    Board board = boardMapper.toEntity(request);
    board.setOwner(currentUser);

    board = boardRepository.save(board);

    return boardMapper.toBoardResponse(board);
  }

  @Override
  public BoardResponse updateBoard(Long boardId, UpdateBoardRequest request) {

    User currentUser = getCurrentUser();

    Board board = getOwnedBoard(boardId);

    if (request.name() != null
        && boardRepository.existsByOwnerAndNameIgnoreCaseAndDeletedFalseAndIdNot(
            currentUser, request.name(), boardId)) {

      throw new BadRequestException("Board with the same name already exists.");
    }

    boardMapper.updateBoardFromRequest(request, board);

    board = boardRepository.save(board);

    return boardMapper.toBoardResponse(board);
  }

  @Override
  public BoardResponse getBoard(Long boardId) {

    return boardMapper.toBoardResponse(getOwnedBoard(boardId));
  }

  @Override
  public BoardListResponse getAccessibleBoards() {

    User currentUser = getCurrentUser();

    List<Board> boards = boardRepository.findAllByOwnerAndDeletedFalse(currentUser);

    // todo: update to accessible boards
    long ownedBoards = boardRepository.countByOwnerAndDeletedFalse(currentUser);

    List<BoardResponse> boardResponses = boardMapper.toBoardResponses(boards);

    return new BoardListResponse(boardResponses, boardResponses.size(), 3, ownedBoards < 3);
  }

  @Override
  public void softDeleteBoard(Long boardId) {

    Board board = getOwnedBoard(boardId);

    board.setDeleted(true);

    boardRepository.save(board);
  }

  @Override
  public List<BoardColorResponse> getColors() {

    return Arrays.stream(BoardColor.values())
        .map(color -> new BoardColorResponse(color.name(), color.getHexCode()))
        .toList();
  }

  // ==========================================================
  // Private Helper Methods
  // ==========================================================

  private Board getOwnedBoard(Long boardId) {

    User currentUser = getCurrentUser();

    return boardRepository
        .findByIdAndOwnerAndDeletedFalse(boardId, currentUser)
        .orElseThrow(
            () -> new ResourceNotFoundException("Board not found with id: ", boardId.toString()));
  }

  private User getCurrentUser() {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    String email = authentication.getName();

    return userRepository
        .findByEmail(email)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with email: ", email));
  }
}
