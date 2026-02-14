package com.shbvn.jms.dto.mapper;

import com.shbvn.jms.dto.response.UserResponse;
import com.shbvn.jms.dto.response.WorkspaceMemberResponse;
import com.shbvn.jms.model.WorkspaceMember;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-13T16:54:26+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23.0.2 (Homebrew)"
)
@Component
public class WorkspaceMemberMapperImpl implements WorkspaceMemberMapper {

    @Autowired
    private UserMapper userMapper;

    @Override
    public WorkspaceMemberResponse toResponse(WorkspaceMember workspaceMember) {
        if ( workspaceMember == null ) {
            return null;
        }

        UserResponse user = null;
        String id = null;
        String userId = null;
        String workspaceId = null;
        String message = null;

        user = userMapper.toResponse( workspaceMember.getUser() );
        id = workspaceMember.getId();
        userId = workspaceMember.getUserId();
        workspaceId = workspaceMember.getWorkspaceId();
        message = workspaceMember.getMessage();

        String role = enumToString(workspaceMember.getRole());

        WorkspaceMemberResponse workspaceMemberResponse = new WorkspaceMemberResponse( id, userId, workspaceId, message, role, user );

        return workspaceMemberResponse;
    }
}
