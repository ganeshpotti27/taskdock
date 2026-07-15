package com.taskdock.taskdock_api.mappers;

import com.taskdock.taskdock_api.dtos.boardlists.BoardListResponse;
import com.taskdock.taskdock_api.dtos.boardlists.CreateBoardListRequest;
import com.taskdock.taskdock_api.dtos.boardlists.UpdateBoardListRequest;
import com.taskdock.taskdock_api.entities.BoardList;
import java.util.List;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface BoardListMapper {

  BoardList toEntity(CreateBoardListRequest request);

  BoardListResponse toBoardListResponse(BoardList boardList);

  List<BoardListResponse> toBoardListResponses(List<BoardList> boardLists);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateBoardListFromRequest(
      UpdateBoardListRequest request, @MappingTarget BoardList boardList);
}
