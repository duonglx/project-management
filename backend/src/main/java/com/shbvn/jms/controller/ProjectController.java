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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectMapper projectMapper;
    private final ProjectMemberMapper projectMemberMapper;

    public ProjectController(ProjectService projectService,
                             ProjectMapper projectMapper,
                             ProjectMemberMapper projectMemberMapper) {
        this.projectService = projectService;
        this.projectMapper = projectMapper;
        this.projectMemberMapper = projectMemberMapper;
    }

    @GetMapping
    public ResponseEntity<Page<ProjectResponse>> getProjects(
            @RequestParam String workspaceId,
            Pageable pageable) {
        Page<Project> projects = projectService.getProjectsByWorkspaceId(workspaceId, pageable);
        Page<ProjectResponse> response = projects.map(projectMapper::toResponse);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody CreateProjectRequest request) {
        Project project = Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .priority(request.getPriority())
                .status(request.getStatus())
                .startDate(request.getStartDate() != null ? request.getStartDate().toLocalDate() : null)
                .endDate(request.getEndDate() != null ? request.getEndDate().toLocalDate() : null)
                .teamLead(request.getTeamLead())
                .workspaceId(request.getWorkspaceId())
                .progress(request.getProgress() != null ? request.getProgress() : 0)
                .build();

        Project created = projectService.createProject(project);
        ProjectResponse response = projectMapper.toResponse(created);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable String id) {
        Project project = projectService.getProjectById(id);
        ProjectResponse response = projectMapper.toResponse(project);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable String id,
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

        Project updated = projectService.updateProject(id, updates);
        ProjectResponse response = projectMapper.toResponse(updated);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable String id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/members")
    public ResponseEntity<ProjectMemberResponse> addMember(
            @PathVariable String id,
            @Valid @RequestBody AddMemberRequest request) {
        ProjectMember member = projectService.addMember(id, request.getUserId());
        ProjectMemberResponse response = projectMemberMapper.toResponse(member);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
