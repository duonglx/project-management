package com.shbvn.jms.dto.mapper;

import com.shbvn.jms.dto.response.WorkspaceMemberResponse;
import com.shbvn.jms.model.WorkspaceMember;
import com.shbvn.jms.model.enums.WorkspaceRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface WorkspaceMemberMapper {

    @Mapping(target = "role", expression = "java(enumToString(workspaceMember.getRole()))")
    @Mapping(target = "user", source = "workspaceMember.user")
    WorkspaceMemberResponse toResponse(WorkspaceMember workspaceMember);

    default String enumToString(WorkspaceRole role) {
        return role != null ? role.name() : null;
    }
}
