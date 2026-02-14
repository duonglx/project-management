package com.shbvn.jms.dto.mapper;

import com.shbvn.jms.dto.response.ProjectMemberResponse;
import com.shbvn.jms.dto.response.UserResponse;
import com.shbvn.jms.model.ProjectMember;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-14T15:19:59+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23.0.2 (Homebrew)"
)
@Component
public class ProjectMemberMapperImpl implements ProjectMemberMapper {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ProjectMemberResponse toResponse(ProjectMember projectMember) {
        if ( projectMember == null ) {
            return null;
        }

        UserResponse user = null;
        String id = null;
        String userId = null;
        String projectId = null;

        user = userMapper.toResponse( projectMember.getUser() );
        id = projectMember.getId();
        userId = projectMember.getUserId();
        projectId = projectMember.getProjectId();

        ProjectMemberResponse projectMemberResponse = new ProjectMemberResponse( id, userId, projectId, user );

        return projectMemberResponse;
    }
}
