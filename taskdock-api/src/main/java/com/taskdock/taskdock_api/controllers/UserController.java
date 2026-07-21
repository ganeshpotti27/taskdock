package com.taskdock.taskdock_api.controllers;

import com.taskdock.taskdock_api.dtos.users.ChangePasswordRequest;
import com.taskdock.taskdock_api.dtos.users.UpdateUserProfileRequest;
import com.taskdock.taskdock_api.dtos.users.UserProfileResponse;
import com.taskdock.taskdock_api.services.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserController {

  UserService userService;

  @GetMapping
  public ResponseEntity<UserProfileResponse> getProfile() {
    return ResponseEntity.ok(userService.getProfile());
  }

  @PatchMapping
  public ResponseEntity<UserProfileResponse> updateProfile(
      @Valid @RequestBody UpdateUserProfileRequest request) {
    return ResponseEntity.ok(userService.updateProfile(request));
  }

  @PatchMapping("/profile-image")
  public ResponseEntity<UserProfileResponse> updateProfileImage(@RequestPart MultipartFile file) {
    return ResponseEntity.ok(userService.updateProfileImage(file));
  }

  @DeleteMapping("/profile-image")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public ResponseEntity<Void> deleteProfileImage() {
    userService.deleteProfileImage();
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/change-password")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void changePassword(@Valid @RequestBody ChangePasswordRequest request) {

    userService.changePassword(request);
  }
}
