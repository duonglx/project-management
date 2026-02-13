package com.shbvn.jms.dto.mapper;

import com.shbvn.jms.dto.response.ProjectResponse;
import com.shbvn.jms.model.Project;
import com.shbvn.jms.model.enums.Priority;
import com.shbvn.jms.model.enums.ProjectStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {TaskMapper.class, ProjectMemberMapper.class, UserMapper.class})
public interface ProjectMapper {

    @Mapping(target = "priority", expression = "java(enumToString(project.getPriority()))")
    @Mapping(target = "status", expression = "java(enumToString(project.getStatus()))")
    @Mapping(target = "tasks", source = "project.tasks")
    @Mapping(target = "members", source = "project.members")
    ProjectResponse toResponse(Project project);

    default String enumToString(Priority priority) {
        return priority != null ? priority.name() : null;
    }

    default String enumToString(ProjectStatus status) {
        return status != null ? status.name() : null;
    }
}
