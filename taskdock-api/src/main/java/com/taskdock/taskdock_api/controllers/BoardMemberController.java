package com.taskdock.taskdock_api.controllers;

import com.taskdock.taskdock_api.dtos.members.InviteMemberRequest;
import com.taskdock.taskdock_api.dtos.members.MemberListResponse;
import com.taskdock.taskdock_api.dtos.members.MemberResponse;
import com.taskdock.taskdock_api.dtos.members.UpdateMemberRoleRequest;
import com.taskdock.taskdock_api.services.BoardMemberService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/boards/{boardId}/members")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BoardMemberController {

  BoardMemberService boardMemberService;

  @GetMapping
  public ResponseEntity<MemberListResponse> getMembers(@PathVariable Long boardId) {
    return ResponseEntity.ok(boardMemberService.getBoardMembers(boardId));
  }

  @PostMapping
  public ResponseEntity<MemberResponse> inviteMember(
      @PathVariable Long boardId, @RequestBody @Valid InviteMemberRequest inviteMemberRequest) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(boardMemberService.inviteMember(boardId, inviteMemberRequest));
  }

  @PatchMapping("/{memberId}")
  public ResponseEntity<MemberResponse> updateMemberRole(
      @PathVariable Long boardId,
      @PathVariable Long memberId,
      @RequestBody @Valid UpdateMemberRoleRequest updateMemberRoleRequest) {
    return ResponseEntity.ok(
        boardMemberService.updateMemberRole(boardId, memberId, updateMemberRoleRequest));
  }

  @DeleteMapping("/{memberId}")
  public ResponseEntity<Void> deleteBoardMember(
      @PathVariable Long boardId, @PathVariable Long memberId) {
    boardMemberService.deleteMember(boardId, memberId);
    return ResponseEntity.noContent().build();
  }
}
