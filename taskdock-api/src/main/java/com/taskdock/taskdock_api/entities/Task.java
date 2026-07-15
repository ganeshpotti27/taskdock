package com.taskdock.taskdock_api.entities;

import com.taskdock.taskdock_api.enums.TaskPriority;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "tasks",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"board_list_id", "position"})})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Task extends BaseEntity {

  @NotBlank
  @Size(min = 2, max = 200)
  @Column(nullable = false, length = 200)
  String title;

  @Size(max = 5000)
  @Column(length = 5000)
  String description;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  TaskPriority priority;

  @Column(nullable = false)
  LocalDateTime dueDate;

  @Column(nullable = false)
  int position;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "board_list_id", nullable = false)
  BoardList boardList;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "assignee_id")
  User assignee;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "created_by", nullable = false)
  User createdBy;
}
