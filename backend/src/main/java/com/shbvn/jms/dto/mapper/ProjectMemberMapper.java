package com.shbvn.jms.dto.mapper;

import com.shbvn.jms.dto.response.ProjectMemberResponse;
import com.shbvn.jms.model.ProjectMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface ProjectMemberMapper {

    @Mapping(target = "user", source = "projectMember.user")
    ProjectMemberResponse toResponse(ProjectMember projectMember);
}
