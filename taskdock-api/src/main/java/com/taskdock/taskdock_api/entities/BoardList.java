package com.taskdock.taskdock_api.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(
    name = "board_lists",
    uniqueConstraints = {
      @UniqueConstraint(columnNames = {"board_id", "position"}),
      @UniqueConstraint(columnNames = {"board_id", "name"})
    })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardList extends BaseEntity {

  @NotBlank
  @Size(min = 2, max = 100)
  @Column(nullable = false, length = 100)
  String name;

  @Column(nullable = false)
  Integer position;

  @Builder.Default
  @Column(nullable = false)
  boolean archived = false;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "board_id", nullable = false)
  Board board;
}
