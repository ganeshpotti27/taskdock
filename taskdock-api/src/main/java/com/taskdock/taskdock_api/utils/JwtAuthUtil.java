package com.taskdock.taskdock_api.security;

import com.taskdock.taskdock_api.entities.User;
import com.taskdock.taskdock_api.exceptions.ResourceNotFoundException;
import com.taskdock.taskdock_api.repositories.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class JwtAuthUtil {

  UserRepository userRepository;

  public Authentication getAuthentication() {
    return SecurityContextHolder.getContext().getAuthentication();
  }

  public String getCurrentUserEmail() {
    return getAuthentication().getName();
  }

  public User getCurrentUser() {
    return userRepository
        .findByEmail(getCurrentUserEmail())
        .orElseThrow(
            () ->
                new ResourceNotFoundException(
                    "User not found with email: ", getCurrentUserEmail()));
  }
}
