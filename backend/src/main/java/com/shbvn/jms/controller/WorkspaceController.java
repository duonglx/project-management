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
import com.shbvn.jms.security.CustomUserDetails;
import com.shbvn.jms.service.WorkspaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workspaces")
@RequiredArgsConstructor
public class WorkspaceController {

    private final WorkspaceService workspaceService;
    private final WorkspaceMapper workspaceMapper;
    private final WorkspaceMemberMapper workspaceMemberMapper;

    @GetMapping
    public ResponseEntity<Page<WorkspaceResponse>> getWorkspaces(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable) {
        Page<Workspace> workspaces = workspaceService.getWorkspacesByUserId(userDetails.getId(), pageable);
        Page<WorkspaceResponse> response = workspaces.map(workspaceMapper::toResponse);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("@perm.isAdminWorkspace()")
    public ResponseEntity<WorkspaceResponse> createWorkspace(
            @Valid @RequestBody CreateWorkspaceRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Workspace workspace = Workspace.builder()
                .name(request.getName())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .ownerId(request.getOwnerId() != null ? request.getOwnerId() : userDetails.getId())
                .build();

        Workspace created = workspaceService.createWorkspace(workspace,
                request.getOwnerId() != null ? request.getOwnerId() : userDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(workspaceMapper.toResponse(created));
    }

    @GetMapping("/{workspaceId}")
    @PreAuthorize("@perm.isMember(#workspaceId)")
    public ResponseEntity<WorkspaceResponse> getWorkspaceById(@PathVariable String workspaceId) {
        Workspace workspace = workspaceService.getWorkspaceById(workspaceId);
        return ResponseEntity.ok(workspaceMapper.toResponse(workspace));
    }

    @PutMapping("/{workspaceId}")
    @PreAuthorize("@perm.check(#workspaceId, 'workspace:manage_settings')")
    public ResponseEntity<WorkspaceResponse> updateWorkspace(
            @PathVariable String workspaceId,
            @Valid @RequestBody UpdateWorkspaceRequest request) {
        Workspace updates = Workspace.builder()
                .name(request.getName())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .build();
        Workspace updated = workspaceService.updateWorkspace(workspaceId, updates);
        return ResponseEntity.ok(workspaceMapper.toResponse(updated));
    }

    @DeleteMapping("/{workspaceId}")
    @PreAuthorize("@perm.check(#workspaceId, 'workspace:manage_settings')")
    public ResponseEntity<Void> deleteWorkspace(@PathVariable String workspaceId) {
        workspaceService.deleteWorkspace(workspaceId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{workspaceId}/members")
    @PreAuthorize("@perm.isMember(#workspaceId)")
    public ResponseEntity<Page<WorkspaceMemberResponse>> getMembers(
            @PathVariable String workspaceId, Pageable pageable) {
        Page<WorkspaceMember> members = workspaceService.getMembers(workspaceId, pageable);
        return ResponseEntity.ok(members.map(workspaceMemberMapper::toResponse));
    }

    @PostMapping("/{workspaceId}/members")
    @PreAuthorize("@perm.check(#workspaceId, 'workspace:manage_members')")
    public ResponseEntity<WorkspaceMemberResponse> addMember(
            @PathVariable String workspaceId,
            @Valid @RequestBody AddMemberRequest request) {
        WorkspaceRole role = request.getRole() != null ? request.getRole() : WorkspaceRole.MEMBER;
        WorkspaceMember member = workspaceService.addMember(workspaceId, request.getUserId(), role, request.getMessage());
        return ResponseEntity.status(HttpStatus.CREATED).body(workspaceMemberMapper.toResponse(member));
    }

    @DeleteMapping("/{workspaceId}/members/{userId}")
    @PreAuthorize("@perm.check(#workspaceId, 'workspace:manage_members')")
    public ResponseEntity<Void> removeMember(@PathVariable String workspaceId, @PathVariable String userId) {
        workspaceService.removeMember(workspaceId, userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{workspaceId}/transfer-ownership")
    @PreAuthorize("@perm.check(#workspaceId, 'workspace:manage_settings')")
    public ResponseEntity<Void> transferOwnership(
            @PathVariable String workspaceId,
            @RequestBody java.util.Map<String, String> body) {
        workspaceService.transferOwnership(workspaceId, body.get("newOwnerId"));
        return ResponseEntity.ok().build();
    }
}
