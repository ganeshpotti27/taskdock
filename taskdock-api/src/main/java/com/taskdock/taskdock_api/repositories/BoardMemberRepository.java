package com.taskdock.taskdock_api.repositories;

import com.taskdock.taskdock_api.entities.Board;
import com.taskdock.taskdock_api.entities.BoardMember;
import com.taskdock.taskdock_api.entities.User;
import com.taskdock.taskdock_api.enums.BoardRole;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardMemberRepository extends JpaRepository<BoardMember, Long> {

  List<BoardMember> findAllByBoard(Board board);

  List<BoardMember> findAllByUser(User user);

  Optional<BoardMember> findByIdAndBoard(Long id, Board board);

  Optional<BoardMember> findByBoardAndUser(Board board, User user);

  Optional<BoardMember> findByBoardIdAndUserId(Long boardId, Long userId);

  boolean existsByBoardAndUser(Board board, User user);

  boolean existsByBoardIdAndUserId(Long boardId, Long userId);

  long countByBoard(Board board);

  void delete(BoardMember member);

  @Query(
      """
            select bm.role
            from BoardMember bm
            where bm.board.id = :boardId
            and bm.user.id = :userId
            """)
  Optional<BoardRole> findRoleByBoardIdAndUserId(
      @Param("boardId") Long boardId, @Param("userId") Long userId);
}
