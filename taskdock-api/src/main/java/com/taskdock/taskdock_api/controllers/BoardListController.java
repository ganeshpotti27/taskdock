package com.taskdock.taskdock_api.controllers;

import com.taskdock.taskdock_api.dtos.boardlists.BoardListResponse;
import com.taskdock.taskdock_api.dtos.boardlists.BoardListsResponse;
import com.taskdock.taskdock_api.dtos.boardlists.CreateBoardListRequest;
import com.taskdock.taskdock_api.dtos.boardlists.ReorderBoardListsRequest;
import com.taskdock.taskdock_api.dtos.boardlists.UpdateBoardListRequest;
import com.taskdock.taskdock_api.services.BoardListService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/boards/{boardId}/lists")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BoardListController {

  BoardListService boardListService;

  @GetMapping
  public ResponseEntity<BoardListsResponse> getActiveLists(@PathVariable Long boardId) {

    return ResponseEntity.ok(boardListService.getActiveLists(boardId));
  }

  @GetMapping("/archived")
  public ResponseEntity<BoardListsResponse> getArchivedLists(@PathVariable Long boardId) {

    return ResponseEntity.ok(boardListService.getArchivedLists(boardId));
  }

  @PostMapping
  public ResponseEntity<BoardListResponse> createList(
      @PathVariable Long boardId, @Valid @RequestBody CreateBoardListRequest request) {

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(boardListService.createList(boardId, request));
  }

  @PatchMapping("/{listId}")
  public ResponseEntity<BoardListResponse> updateList(
      @PathVariable Long boardId,
      @PathVariable Long listId,
      @Valid @RequestBody UpdateBoardListRequest request) {

    return ResponseEntity.ok(boardListService.updateList(boardId, listId, request));
  }

  @PatchMapping("/reorder")
  public ResponseEntity<Void> reorderLists(
      @PathVariable Long boardId, @Valid @RequestBody ReorderBoardListsRequest request) {

    boardListService.reorderLists(boardId, request);

    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{listId}/archive")
  public ResponseEntity<Void> archiveList(@PathVariable Long boardId, @PathVariable Long listId) {

    boardListService.archiveList(boardId, listId);

    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{listId}/restore")
  public ResponseEntity<Void> restoreList(@PathVariable Long boardId, @PathVariable Long listId) {

    boardListService.restoreList(boardId, listId);

    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{listId}")
  public ResponseEntity<Void> deleteArchivedList(
      @PathVariable Long boardId, @PathVariable Long listId) {

    boardListService.deleteArchivedList(boardId, listId);

    return ResponseEntity.noContent().build();
  }
}
