package com.taskdock.taskdock_api.services.impl;

import com.taskdock.taskdock_api.dtos.members.*;
import com.taskdock.taskdock_api.entities.Board;
import com.taskdock.taskdock_api.entities.BoardMember;
import com.taskdock.taskdock_api.entities.User;
import com.taskdock.taskdock_api.exceptions.BadRequestException;
import com.taskdock.taskdock_api.exceptions.ResourceNotFoundException;
import com.taskdock.taskdock_api.mappers.BoardMemberMapper;
import com.taskdock.taskdock_api.repositories.BoardMemberRepository;
import com.taskdock.taskdock_api.repositories.BoardRepository;
import com.taskdock.taskdock_api.repositories.UserRepository;
import com.taskdock.taskdock_api.services.BoardMemberService;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BoardMemberServiceImpl implements BoardMemberService {

  BoardRepository boardRepository;
  BoardMemberRepository boardMemberRepository;
  UserRepository userRepository;
  BoardMemberMapper boardMemberMapper;

  @Override
  @PreAuthorize("@security.canManageMembers(#boardId)")
  public MemberResponse inviteMember(Long boardId, InviteMemberRequest request) {

    Board board = getBoard(boardId);

    User user =
        userRepository
            .findByEmail(request.email())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException("User not found with email: ", request.email()));

    if (board.getOwner().getId().equals(user.getId())) {
      throw new BadRequestException("Owner cannot be invited.");
    }

    if (boardMemberRepository.existsByBoardAndUser(board, user)) {
      throw new BadRequestException("User is already a member.");
    }

    BoardMember member = BoardMember.builder().board(board).user(user).role(request.role()).build();

    member = boardMemberRepository.save(member);

    return boardMemberMapper.toMemberResponse(member);
  }

  @Override
  @PreAuthorize("@security.canManageMembers(#boardId)")
  public MemberResponse updateMemberRole(
      Long boardId, Long memberId, UpdateMemberRoleRequest request) {

    Board board = getBoard(boardId);

    BoardMember member =
        boardMemberRepository
            .findByIdAndBoard(memberId, board)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Member not found with id: ", memberId.toString()));

    if (member.getUser().getId().equals(board.getOwner().getId())) {
      throw new BadRequestException("Owner role cannot be changed.");
    }

    member.setRole(request.role());

    member = boardMemberRepository.save(member);

    return boardMemberMapper.toMemberResponse(member);
  }

  @Override
  @PreAuthorize("@security.canManageMembers(#boardId)")
  public void deleteMember(Long boardId, Long memberId) {

    Board board = getBoard(boardId);

    BoardMember member =
        boardMemberRepository
            .findByIdAndBoard(memberId, board)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Member not found with id: ", memberId.toString()));

    if (member.getUser().getId().equals(board.getOwner().getId())) {
      throw new BadRequestException("Owner cannot be removed.");
    }

    boardMemberRepository.delete(member);
  }

  @Override
  @PreAuthorize("@security.canViewMembers(#boardId)")
  public MemberListResponse getBoardMembers(Long boardId) {

    Board board = getBoard(boardId);

    List<BoardMember> members = boardMemberRepository.findAllByBoard(board);

    return new MemberListResponse(boardMemberMapper.toMemberResponses(members));
  }

  private Board getBoard(Long boardId) {

    return boardRepository
        .findByIdAndDeletedFalse(boardId)
        .orElseThrow(
            () -> new ResourceNotFoundException("Board not found with id: ", boardId.toString()));
  }
}
