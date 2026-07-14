package com.taskdock.taskdock_api.services.impl;

import com.taskdock.taskdock_api.dtos.boards.*;
import com.taskdock.taskdock_api.entities.Board;
import com.taskdock.taskdock_api.entities.BoardMember;
import com.taskdock.taskdock_api.entities.User;
import com.taskdock.taskdock_api.enums.BoardColor;
import com.taskdock.taskdock_api.enums.BoardRole;
import com.taskdock.taskdock_api.exceptions.BadRequestException;
import com.taskdock.taskdock_api.exceptions.ResourceNotFoundException;
import com.taskdock.taskdock_api.mappers.BoardMapper;
import com.taskdock.taskdock_api.repositories.BoardMemberRepository;
import com.taskdock.taskdock_api.repositories.BoardRepository;
import com.taskdock.taskdock_api.repositories.UserRepository;
import com.taskdock.taskdock_api.security.JwtAuthUtil;
import com.taskdock.taskdock_api.services.BoardService;
import java.util.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BoardServiceImpl implements BoardService {

  BoardRepository boardRepository;
  BoardMemberRepository boardMemberRepository;
  UserRepository userRepository;
  BoardMapper boardMapper;
  JwtAuthUtil jwtAuthUtil;

  @Override
  public BoardResponse createBoard(CreateBoardRequest request) {

    User currentUser = jwtAuthUtil.getCurrentUser();

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

    BoardMember ownerMember =
        BoardMember.builder().board(board).user(currentUser).role(BoardRole.OWNER).build();

    boardMemberRepository.save(ownerMember);

    return boardMapper.toBoardResponse(board);
  }

  @Override
  @PreAuthorize("@security.canEditBoard(#boardId)")
  public BoardResponse updateBoard(Long boardId, UpdateBoardRequest request) {

    User currentUser = jwtAuthUtil.getCurrentUser();

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
  @PreAuthorize("@security.canViewBoard(#boardId)")
  public BoardResponse getBoard(Long boardId) {

    return boardMapper.toBoardResponse(getAccessibleBoard(boardId));
  }

  @Override
  public BoardListResponse getAccessibleBoards() {

    User currentUser = jwtAuthUtil.getCurrentUser();

    List<Board> ownedBoards = boardRepository.findAllByOwnerAndDeletedFalse(currentUser);

    List<BoardMember> memberships = boardMemberRepository.findAllByUser(currentUser);

    Map<Long, Board> accessibleBoards = new LinkedHashMap<>();

    ownedBoards.forEach(board -> accessibleBoards.put(board.getId(), board));

    memberships.stream()
        .map(BoardMember::getBoard)
        .filter(board -> !board.isDeleted())
        .forEach(board -> accessibleBoards.putIfAbsent(board.getId(), board));

    long ownedCount = boardRepository.countByOwnerAndDeletedFalse(currentUser);

    List<BoardResponse> responses =
        boardMapper.toBoardResponses(new ArrayList<>(accessibleBoards.values()));

    return new BoardListResponse(responses, responses.size(), 3, ownedCount < 3);
  }

  @Override
  @PreAuthorize("@security.canDeleteBoard(#boardId)")
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

  private Board getAccessibleBoard(Long boardId) {

    return boardRepository
        .findByIdAndDeletedFalse(boardId)
        .orElseThrow(
            () -> new ResourceNotFoundException("Board not found with id: ", boardId.toString()));
  }

  private Board getOwnedBoard(Long boardId) {

    User currentUser = jwtAuthUtil.getCurrentUser();

    return boardRepository
        .findByIdAndOwnerAndDeletedFalse(boardId, currentUser)
        .orElseThrow(
            () -> new ResourceNotFoundException("Board not found with id: ", boardId.toString()));
  }
}
