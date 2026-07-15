package com.taskdock.taskdock_api.repositories;

import com.taskdock.taskdock_api.entities.BoardList;
import com.taskdock.taskdock_api.entities.Task;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

  Optional<Task> findByIdAndBoardListBoardId(Long taskId, Long boardId);

  List<Task> findAllByBoardListOrderByPositionAsc(BoardList boardList);

  @Query(
      """
            select coalesce(max(t.position), 0)
            from Task t
            where t.boardList = :boardList
            """)
  Integer findMaxPosition(BoardList boardList);

  long countByBoardList(BoardList boardList);

  List<Task> findAllByBoardListAndPositionGreaterThanOrderByPositionAsc(
      BoardList boardList, int position);

  List<Task> findAllByBoardListAndPositionGreaterThanEqualOrderByPositionAsc(
      BoardList boardList, int position);
}
