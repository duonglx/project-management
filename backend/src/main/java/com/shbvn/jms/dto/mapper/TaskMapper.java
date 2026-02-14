package com.shbvn.jms.dto.mapper;

import com.shbvn.jms.dto.response.TaskResponse;
import com.shbvn.jms.dto.response.TaskStatusResponse;
import com.shbvn.jms.model.Task;
import com.shbvn.jms.model.TaskStatusEntity;
import com.shbvn.jms.model.enums.Priority;
import com.shbvn.jms.model.enums.TaskType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {UserMapper.class, CommentMapper.class})
public interface TaskMapper {

    @Mapping(target = "type", expression = "java(enumToString(task.getType()))")
    @Mapping(target = "priority", expression = "java(enumToString(task.getPriority()))")
    @Mapping(target = "assignee", source = "task.assignee")
    @Mapping(target = "comments", source = "task.comments")
    @Mapping(target = "statusId", source = "task.statusId")
    @Mapping(target = "taskStatus", source = "task.taskStatus", qualifiedByName = "toTaskStatusResponse")
    TaskResponse toResponse(Task task);

    @Named("toTaskStatusResponse")
    default TaskStatusResponse toTaskStatusResponse(TaskStatusEntity entity) {
        if (entity == null) return null;
        return new TaskStatusResponse(
                entity.getId(), entity.getWorkspaceId(), entity.getName(), entity.getSlug(),
                entity.getColor(), entity.getCategory().name(), entity.getPosition(),
                entity.getIsDefault(), entity.getCreatedAt(), entity.getUpdatedAt()
        );
    }

    default String enumToString(TaskType type) {
        return type != null ? type.name() : null;
    }

    default String enumToString(Priority priority) {
        return priority != null ? priority.name() : null;
    }
}
