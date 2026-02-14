package com.shbvn.jms.dto.mapper;

import com.shbvn.jms.dto.response.ProjectResponse;
import com.shbvn.jms.dto.response.UserResponse;
import com.shbvn.jms.dto.response.WorkspaceMemberResponse;
import com.shbvn.jms.dto.response.WorkspaceResponse;
import com.shbvn.jms.model.Project;
import com.shbvn.jms.model.Workspace;
import com.shbvn.jms.model.WorkspaceMember;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-14T15:19:59+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23.0.2 (Homebrew)"
)
@Component
public class WorkspaceMapperImpl implements WorkspaceMapper {

    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private WorkspaceMemberMapper workspaceMemberMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public WorkspaceResponse toResponse(Workspace workspace) {
        if ( workspace == null ) {
            return null;
        }

        List<WorkspaceMemberResponse> members = null;
        List<ProjectResponse> projects = null;
        UserResponse owner = null;
        String id = null;
        String name = null;
        String slug = null;
        String description = null;
        Map<String, Object> settings = null;
        String ownerId = null;
        String imageUrl = null;
        LocalDateTime createdAt = null;
        LocalDateTime updatedAt = null;

        members = workspaceMemberListToWorkspaceMemberResponseList( workspace.getMembers() );
        projects = projectListToProjectResponseList( workspace.getProjects() );
        owner = userMapper.toResponse( workspace.getOwner() );
        id = workspace.getId();
        name = workspace.getName();
        slug = workspace.getSlug();
        description = workspace.getDescription();
        Map<String, Object> map = workspace.getSettings();
        if ( map != null ) {
            settings = new LinkedHashMap<String, Object>( map );
        }
        ownerId = workspace.getOwnerId();
        imageUrl = workspace.getImageUrl();
        createdAt = workspace.getCreatedAt();
        updatedAt = workspace.getUpdatedAt();

        WorkspaceResponse workspaceResponse = new WorkspaceResponse( id, name, slug, description, settings, ownerId, imageUrl, createdAt, updatedAt, members, projects, owner );

        return workspaceResponse;
    }

    protected List<WorkspaceMemberResponse> workspaceMemberListToWorkspaceMemberResponseList(List<WorkspaceMember> list) {
        if ( list == null ) {
            return null;
        }

        List<WorkspaceMemberResponse> list1 = new ArrayList<WorkspaceMemberResponse>( list.size() );
        for ( WorkspaceMember workspaceMember : list ) {
            list1.add( workspaceMemberMapper.toResponse( workspaceMember ) );
        }

        return list1;
    }

    protected List<ProjectResponse> projectListToProjectResponseList(List<Project> list) {
        if ( list == null ) {
            return null;
        }

        List<ProjectResponse> list1 = new ArrayList<ProjectResponse>( list.size() );
        for ( Project project : list ) {
            list1.add( projectMapper.toResponse( project ) );
        }

        return list1;
    }
}
