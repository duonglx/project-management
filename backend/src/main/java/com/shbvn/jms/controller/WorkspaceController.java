package com.shbvn.jms.controller;

import com.shbvn.jms.dto.mapper.WorkspaceMapper;
import com.shbvn.jms.dto.mapper.WorkspaceMemberMapper;
import com.shbvn.jms.dto.request.AddMemberRequest;
import com.shbvn.jms.dto.request.CreateWorkspaceRequest;
import com.shbvn.jms.dto.request.UpdateWorkspaceRequest;
import com.shbvn.jms.dto.response.WorkspaceMemberResponse;
import com.shbvn.jms.dto.response.WorkspaceResponse;
import com.shbvn.jms.model.Workspace;
import com.shbvn.jms.model.WorkspaceMember;
import com.shbvn.jms.model.enums.WorkspaceRole;
import com.shbvn.jms.service.WorkspaceService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/workspaces")
public class WorkspaceController {

    private final WorkspaceService workspaceService;
    private final WorkspaceMapper workspaceMapper;
    private final WorkspaceMemberMapper workspaceMemberMapper;

    public WorkspaceController(WorkspaceService workspaceService,
                               WorkspaceMapper workspaceMapper,
                               WorkspaceMemberMapper workspaceMemberMapper) {
        this.workspaceService = workspaceService;
        this.workspaceMapper = workspaceMapper;
        this.workspaceMemberMapper = workspaceMemberMapper;
    }

    @GetMapping
    public ResponseEntity<Page<WorkspaceResponse>> getWorkspaces(
            @RequestParam String userId,
            Pageable pageable) {
        Page<Workspace> workspaces = workspaceService.getWorkspacesByUserId(userId, pageable);
        Page<WorkspaceResponse> response = workspaces.map(workspaceMapper::toResponse);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<WorkspaceResponse> createWorkspace(@Valid @RequestBody CreateWorkspaceRequest request) {
        Workspace workspace = Workspace.builder()
                .name(request.getName())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .ownerId(request.getOwnerId())
                .build();

        Workspace created = workspaceService.createWorkspace(workspace, request.getOwnerId());
        WorkspaceResponse response = workspaceMapper.toResponse(created);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkspaceResponse> getWorkspaceById(@PathVariable String id) {
        Workspace workspace = workspaceService.getWorkspaceById(id);
        WorkspaceResponse response = workspaceMapper.toResponse(workspace);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkspaceResponse> updateWorkspace(
            @PathVariable String id,
            @Valid @RequestBody UpdateWorkspaceRequest request) {
        Workspace updates = Workspace.builder()
                .name(request.getName())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .build();

        Workspace updated = workspaceService.updateWorkspace(id, updates);
        WorkspaceResponse response = workspaceMapper.toResponse(updated);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkspace(@PathVariable String id) {
        workspaceService.deleteWorkspace(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/members")
    public ResponseEntity<WorkspaceMemberResponse> addMember(
            @PathVariable String id,
            @Valid @RequestBody AddMemberRequest request) {
        WorkspaceRole role = request.getRole() != null ? request.getRole() : WorkspaceRole.MEMBER;
        WorkspaceMember member = workspaceService.addMember(id, request.getUserId(), role, request.getMessage());
        WorkspaceMemberResponse response = workspaceMemberMapper.toResponse(member);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}/members/{userId}")
    public ResponseEntity<Void> removeMember(@PathVariable String id, @PathVariable String userId) {
        workspaceService.removeMember(id, userId);
        return ResponseEntity.noContent().build();
    }
}
