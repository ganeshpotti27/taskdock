package com.taskdock.taskdock_api.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.*;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User extends BaseEntity implements UserDetails {
  @NotBlank
  @Column(nullable = false, length = 100)
  String fullName;

  @Min(value = 13, message = "Age must be at least 13 years.")
  @Max(value = 120, message = "Age must not exceed 120 years.")
  private Integer age;

  @Email
  @NotBlank
  @Column(nullable = false, unique = true)
  String email;

  @NotBlank
  @Column(nullable = false)
  String passwordHash;

  @Builder.Default
  @Column(nullable = false)
  Boolean emailVerified = false;

  @NotBlank
  @Column(nullable = false, unique = true, length = 10)
  @Pattern(regexp = "\\d{10}")
  String phoneNumber;

  @Builder.Default
  @Column(nullable = false)
  Boolean phoneVerified = false;

  @Column(length = 500)
  String profileImageUrl;

  @Column Instant lastLoginAt;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of();
  }

  @Override
  public @Nullable String getPassword() {
    return passwordHash;
  }

  @Override
  public String getUsername() {
    return email;
  }
}
