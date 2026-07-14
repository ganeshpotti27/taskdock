package com.taskdock.taskdock_api.services.impl;

import com.taskdock.taskdock_api.dtos.users.UpdateUserProfileRequest;
import com.taskdock.taskdock_api.dtos.users.UserProfileResponse;
import com.taskdock.taskdock_api.entities.User;
import com.taskdock.taskdock_api.mappers.UserMapper;
import com.taskdock.taskdock_api.repositories.UserRepository;
import com.taskdock.taskdock_api.security.JwtAuthUtil;
import com.taskdock.taskdock_api.services.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService, UserDetailsService {

  UserRepository userRepository;

  UserMapper userMapper;

  JwtAuthUtil jwtAuthUtil;

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
    return null;
  }

  @Override
  public void deleteProfileImage() {}

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository
        .findByEmail(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
  }
}
