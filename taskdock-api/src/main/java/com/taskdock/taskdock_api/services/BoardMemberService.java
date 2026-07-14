package com.taskdock.taskdock_api.services;

import com.taskdock.taskdock_api.dtos.members.InviteMemberRequest;
import com.taskdock.taskdock_api.dtos.members.MemberListResponse;
import com.taskdock.taskdock_api.dtos.members.MemberResponse;
import com.taskdock.taskdock_api.dtos.members.UpdateMemberRoleRequest;
import jakarta.validation.Valid;

public interface BoardMemberService {

  MemberResponse inviteMember(Long boardId, @Valid InviteMemberRequest inviteMemberRequest);

  MemberResponse updateMemberRole(
      Long boardId, Long memberId, @Valid UpdateMemberRoleRequest updateMemberRoleRequest);

  void deleteMember(Long boardId, Long memberId);

  MemberListResponse getBoardMembers(Long boardId);
}
