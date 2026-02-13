package com.shbvn.jms.dto.mapper;

import com.shbvn.jms.dto.response.WorkspaceResponse;
import com.shbvn.jms.model.Workspace;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ProjectMapper.class, WorkspaceMemberMapper.class, UserMapper.class})
public interface WorkspaceMapper {

    @Mapping(target = "members", source = "workspace.members")
    @Mapping(target = "projects", source = "workspace.projects")
    @Mapping(target = "owner", source = "workspace.owner")
    WorkspaceResponse toResponse(Workspace workspace);
}
