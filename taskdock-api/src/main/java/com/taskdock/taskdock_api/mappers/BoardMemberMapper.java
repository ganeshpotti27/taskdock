package com.taskdock.taskdock_api.mappers;

import com.taskdock.taskdock_api.dtos.members.MemberResponse;
import com.taskdock.taskdock_api.dtos.members.UpdateMemberRoleRequest;
import com.taskdock.taskdock_api.entities.BoardMember;
import java.util.List;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface BoardMemberMapper {

  @Mapping(target = "memberId", source = "id")
  @Mapping(target = "userId", source = "user.id")
  @Mapping(target = "email", source = "user.email")
  @Mapping(target = "fullName", source = "user.fullName")
  @Mapping(target = "profileImageUrl", source = "user.profileImageUrl")
  MemberResponse toMemberResponse(BoardMember member);

  List<MemberResponse> toMemberResponses(List<BoardMember> members);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateMemberRoleFromRequest(
      UpdateMemberRoleRequest request, @MappingTarget BoardMember member);
}
