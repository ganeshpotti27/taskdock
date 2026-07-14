package com.taskdock.taskdock_api.entities;

import com.taskdock.taskdock_api.enums.BoardRole;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "board_members",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"board_id", "user_id"})})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BoardMember extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "board_id", nullable = false)
  Board board;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  User user;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  BoardRole role;
}
