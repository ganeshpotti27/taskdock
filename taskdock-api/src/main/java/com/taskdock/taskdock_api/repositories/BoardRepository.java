package com.taskdock.taskdock_api.repositories;

import com.taskdock.taskdock_api.entities.Board;
import com.taskdock.taskdock_api.entities.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {

  long countByOwnerAndDeletedFalse(User owner);

  boolean existsByOwnerAndNameIgnoreCaseAndDeletedFalse(User owner, String name);

  boolean existsByOwnerAndNameIgnoreCaseAndDeletedFalseAndIdNot(
      User owner, String name, Long boardId);

  List<Board> findAllByOwnerAndDeletedFalse(User owner);

  Optional<Board> findByIdAndOwnerAndDeletedFalse(Long boardId, User owner);
}
