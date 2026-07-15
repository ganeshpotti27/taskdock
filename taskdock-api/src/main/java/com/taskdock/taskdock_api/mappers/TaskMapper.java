package com.taskdock.taskdock_api.mappers;

import com.taskdock.taskdock_api.dtos.tasks.CreateTaskRequest;
import com.taskdock.taskdock_api.dtos.tasks.TaskResponse;
import com.taskdock.taskdock_api.dtos.tasks.UpdateTaskRequest;
import com.taskdock.taskdock_api.entities.Task;
import java.util.List;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TaskMapper {

  @Mapping(target = "position", ignore = true)
  @Mapping(target = "boardList", ignore = true)
  @Mapping(target = "assignee", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  Task toEntity(CreateTaskRequest request);

  @Mapping(target = "boardListId", source = "boardList.id")
  @Mapping(target = "assigneeId", source = "assignee.id")
  @Mapping(target = "assigneeName", source = "assignee.fullName")
  @Mapping(target = "assigneeProfileImageUrl", source = "assignee.profileImageUrl")
  @Mapping(target = "createdById", source = "createdBy.id")
  @Mapping(target = "createdByName", source = "createdBy.fullName")
  @Mapping(target = "createdByProfileImageUrl", source = "createdBy.profileImageUrl")
  TaskResponse toTaskResponse(Task task);

  List<TaskResponse> toTaskResponses(List<Task> tasks);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "position", ignore = true)
  @Mapping(target = "boardList", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "assignee", ignore = true)
  void updateTaskFromRequest(UpdateTaskRequest request, @MappingTarget Task task);
}
