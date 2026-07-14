package com.taskdock.taskdock_api.mappers;

import com.taskdock.taskdock_api.dtos.boards.BoardResponse;
import com.taskdock.taskdock_api.dtos.boards.CreateBoardRequest;
import com.taskdock.taskdock_api.dtos.boards.UpdateBoardRequest;
import com.taskdock.taskdock_api.entities.Board;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface BoardMapper {

  Board toEntity(CreateBoardRequest request);

  BoardResponse toBoardResponse(Board board);

  List<BoardResponse> toBoardResponses(List<Board> boards);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateBoardFromRequest(UpdateBoardRequest request, @MappingTarget Board board);
}
