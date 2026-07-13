package com.taskdock.taskdock_api.services;

import com.taskdock.taskdock_api.dtos.users.UpdateUserProfileRequest;
import com.taskdock.taskdock_api.dtos.users.UserProfileResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

  UserProfileResponse updateProfile(UpdateUserProfileRequest request);

  UserProfileResponse getProfile();

  UserProfileResponse updateProfileImage(MultipartFile file);

  void deleteProfileImage();

  UserDetails loadUserByUsername(String username);
}
