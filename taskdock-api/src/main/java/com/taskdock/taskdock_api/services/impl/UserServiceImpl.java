package com.taskdock.taskdock_api.services.impl;

import com.taskdock.taskdock_api.dtos.media.MediaUploadResponse;
import com.taskdock.taskdock_api.dtos.users.ChangePasswordRequest;
import com.taskdock.taskdock_api.dtos.users.UpdateUserProfileRequest;
import com.taskdock.taskdock_api.dtos.users.UserProfileResponse;
import com.taskdock.taskdock_api.entities.User;
import com.taskdock.taskdock_api.exceptions.BadRequestException;
import com.taskdock.taskdock_api.mappers.UserMapper;
import com.taskdock.taskdock_api.repositories.UserRepository;
import com.taskdock.taskdock_api.security.JwtAuthUtil;
import com.taskdock.taskdock_api.services.MediaStorageService;
import com.taskdock.taskdock_api.services.UserService;
import com.taskdock.taskdock_api.utils.CloudinaryFolders;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService, UserDetailsService {

  UserRepository userRepository;

  UserMapper userMapper;

  JwtAuthUtil jwtAuthUtil;

  PasswordEncoder passwordEncoder;

  MediaStorageService mediaStorageService;

  @Override
  public UserProfileResponse updateProfile(UpdateUserProfileRequest request) {

    User user = jwtAuthUtil.getCurrentUser();

    userMapper.updateUserFromRequest(request, user);

    user = userRepository.save(user);

    return userMapper.toUserProfileResponse(user);
  }

  @Override
  public UserProfileResponse getProfile() {
    return userMapper.toUserProfileResponse(jwtAuthUtil.getCurrentUser());
  }

  @Override
  public UserProfileResponse updateProfileImage(MultipartFile file) {

    User user = jwtAuthUtil.getCurrentUser();

    if (user.getProfileImagePublicId() != null && !user.getProfileImagePublicId().isBlank()) {

      mediaStorageService.delete(user.getProfileImagePublicId());
    }

    MediaUploadResponse uploadResponse =
        mediaStorageService.upload(file, CloudinaryFolders.PROFILE_IMAGES);

    user.setProfileImageUrl(uploadResponse.url());
    user.setProfileImagePublicId(uploadResponse.publicId());

    userRepository.save(user);

    return userMapper.toUserProfileResponse(user);
  }

  @Override
  public void deleteProfileImage() {

    User user = jwtAuthUtil.getCurrentUser();

    if (user.getProfileImagePublicId() == null || user.getProfileImagePublicId().isBlank()) {
      return;
    }

    mediaStorageService.delete(user.getProfileImagePublicId());

    user.setProfileImageUrl(null);
    user.setProfileImagePublicId(null);

    userRepository.save(user);
  }

  @Override
  public void changePassword(ChangePasswordRequest request) {

    User currentUser = jwtAuthUtil.getCurrentUser();

    if (!passwordEncoder.matches(request.oldPassword(), currentUser.getPassword())) {

      throw new BadRequestException("Old password is incorrect.");
    }

    currentUser.setPasswordHash(passwordEncoder.encode(request.newPassword()));

    userRepository.save(currentUser);
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository
        .findByEmail(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
  }
}
