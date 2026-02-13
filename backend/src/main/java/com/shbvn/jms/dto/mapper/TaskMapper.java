package com.shbvn.jms.dto.mapper;

import com.shbvn.jms.dto.response.TaskResponse;
import com.shbvn.jms.model.Task;
import com.shbvn.jms.model.enums.Priority;
import com.shbvn.jms.model.enums.TaskStatus;
import com.shbvn.jms.model.enums.TaskType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class, CommentMapper.class})
public interface TaskMapper {

    @Mapping(target = "status", expression = "java(enumToString(task.getStatus()))")
    @Mapping(target = "type", expression = "java(enumToString(task.getType()))")
    @Mapping(target = "priority", expression = "java(enumToString(task.getPriority()))")
    @Mapping(target = "assignee", source = "task.assignee")
    @Mapping(target = "comments", source = "task.comments")
    TaskResponse toResponse(Task task);

    default String enumToString(TaskStatus status) {
        return status != null ? status.name() : null;
    }

    default String enumToString(TaskType type) {
        return type != null ? type.name() : null;
    }

    default String enumToString(Priority priority) {
        return priority != null ? priority.name() : null;
    }
}
