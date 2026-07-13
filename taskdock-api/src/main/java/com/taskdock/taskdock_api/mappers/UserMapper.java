package com.taskdock.taskdock_api.mappers;

import com.taskdock.taskdock_api.dtos.auth.RegisterRequest;
import com.taskdock.taskdock_api.dtos.users.UpdateUserProfileRequest;
import com.taskdock.taskdock_api.dtos.users.UserProfileResponse;
import com.taskdock.taskdock_api.dtos.users.UserResponse;
import com.taskdock.taskdock_api.entities.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserMapper {

  UserProfileResponse toUserProfileResponse(User user);

  UserResponse toUserResponse(User user);

  User toEntity(RegisterRequest request);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateUserFromRequest(UpdateUserProfileRequest request, @MappingTarget User user);
}
