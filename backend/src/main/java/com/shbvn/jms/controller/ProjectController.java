package com.shbvn.jms.controller;

import com.shbvn.jms.dto.mapper.ProjectMapper;
import com.shbvn.jms.dto.mapper.ProjectMemberMapper;
import com.shbvn.jms.dto.request.AddMemberRequest;
import com.shbvn.jms.dto.request.CreateProjectRequest;
import com.shbvn.jms.dto.request.UpdateProjectRequest;
import com.shbvn.jms.dto.response.ProjectMemberResponse;
import com.shbvn.jms.dto.response.ProjectResponse;
import com.shbvn.jms.model.Project;
import com.shbvn.jms.model.ProjectMember;
import com.shbvn.jms.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workspaces/{workspaceId}/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectMapper projectMapper;
    private final ProjectMemberMapper projectMemberMapper;

    @GetMapping
    @PreAuthorize("@perm.isMember(#workspaceId)")
    public ResponseEntity<Page<ProjectResponse>> getProjects(
            @PathVariable String workspaceId,
            Pageable pageable) {
        Page<Project> projects = projectService.getProjectsByWorkspaceId(workspaceId, pageable);
        return ResponseEntity.ok(projects.map(projectMapper::toResponse));
    }

    @PostMapping
    @PreAuthorize("@perm.check(#workspaceId, 'workspace:create_project')")
    public ResponseEntity<ProjectResponse> createProject(
            @PathVariable String workspaceId,
            @Valid @RequestBody CreateProjectRequest request) {
        Project project = Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .priority(request.getPriority())
                .status(request.getStatus())
                .startDate(request.getStartDate() != null ? request.getStartDate().toLocalDate() : null)
                .endDate(request.getEndDate() != null ? request.getEndDate().toLocalDate() : null)
                .teamLead(request.getTeamLead())
                .workspaceId(workspaceId)
                .progress(request.getProgress() != null ? request.getProgress() : 0)
                .build();

        Project created = projectService.createProject(project);
        return ResponseEntity.status(HttpStatus.CREATED).body(projectMapper.toResponse(created));
    }

    @GetMapping("/{projectId}")
    @PreAuthorize("@perm.isMember(#workspaceId)")
    public ResponseEntity<ProjectResponse> getProjectById(
            @PathVariable String workspaceId,
            @PathVariable String projectId) {
        Project project = projectService.getProjectById(projectId);
        return ResponseEntity.ok(projectMapper.toResponse(project));
    }

    @PutMapping("/{projectId}")
    @PreAuthorize("@perm.checkProject(#workspaceId, 'project:update', #projectId)")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable String workspaceId,
            @PathVariable String projectId,
            @Valid @RequestBody UpdateProjectRequest request) {
        Project updates = Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .priority(request.getPriority())
                .status(request.getStatus())
                .startDate(request.getStartDate() != null ? request.getStartDate().toLocalDate() : null)
                .endDate(request.getEndDate() != null ? request.getEndDate().toLocalDate() : null)
                .teamLead(request.getTeamLead())
                .progress(request.getProgress())
                .build();

        Project updated = projectService.updateProject(projectId, updates);
        return ResponseEntity.ok(projectMapper.toResponse(updated));
    }

    @DeleteMapping("/{projectId}")
    @PreAuthorize("@perm.check(#workspaceId, 'workspace:delete_project')")
    public ResponseEntity<Void> deleteProject(
            @PathVariable String workspaceId,
            @PathVariable String projectId) {
        projectService.deleteProject(projectId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{projectId}/members")
    @PreAuthorize("@perm.checkProject(#workspaceId, 'project:manage_members', #projectId)")
    public ResponseEntity<ProjectMemberResponse> addMember(
            @PathVariable String workspaceId,
            @PathVariable String projectId,
            @Valid @RequestBody AddMemberRequest request) {
        ProjectMember member = projectService.addMember(projectId, request.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(projectMemberMapper.toResponse(member));
    }

    @DeleteMapping("/{projectId}/members/{userId}")
    @PreAuthorize("@perm.checkProject(#workspaceId, 'project:manage_members', #projectId)")
    public ResponseEntity<Void> removeMember(
            @PathVariable String workspaceId,
            @PathVariable String projectId,
            @PathVariable String userId) {
        projectService.removeMember(projectId, userId);
        return ResponseEntity.noContent().build();
    }
}
