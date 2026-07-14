package com.taskdock.taskdock_api.entities;

import com.taskdock.taskdock_api.enums.BoardColor;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "boards",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"owner_id", "name"})})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Board extends BaseEntity {

  @NotBlank
  @Size(min = 2, max = 100)
  @Column(nullable = false, length = 100)
  String name;

  @Size(max = 500)
  @Column(length = 500)
  String description;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private BoardColor color;

  @Builder.Default
  @Column(nullable = false)
  boolean starred = false;

  @Builder.Default
  @Column(nullable = false)
  boolean deleted = false;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "owner_id", nullable = false)
  User owner;
}
