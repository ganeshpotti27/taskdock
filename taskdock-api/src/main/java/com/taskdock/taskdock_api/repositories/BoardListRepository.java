package com.taskdock.taskdock_api.repositories;

import com.taskdock.taskdock_api.entities.Board;
import com.taskdock.taskdock_api.entities.BoardList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardListRepository extends JpaRepository<BoardList, Long> {

  List<BoardList> findAllByBoardAndArchivedFalseOrderByPositionAsc(Board board);

  List<BoardList> findAllByBoardAndArchivedTrueOrderByPositionAsc(Board board);

  Optional<BoardList> findByIdAndBoard(Long id, Board board);

  boolean existsByBoardAndNameIgnoreCaseAndArchivedFalse(Board board, String name);

  boolean existsByBoardAndNameIgnoreCaseAndArchivedFalseAndIdNot(
      Board board, String name, Long listId);

  Optional<BoardList> findByIdAndBoardAndArchivedFalse(Long id, Board board);

  @Query(
      """
       select coalesce(max(bl.position), 0)
       from BoardList bl
       where bl.board = :board
       """)
  Integer findMaxPositionByBoard(Board board);

  long countByBoard(Board board);

  long countByBoardAndArchivedFalse(Board board);
}
