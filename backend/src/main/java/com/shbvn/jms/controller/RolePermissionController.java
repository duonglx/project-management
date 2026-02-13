package com.shbvn.jms.controller;

import com.shbvn.jms.dto.request.UpdateRolePermissionsRequest;
import com.shbvn.jms.dto.response.PermissionResponse;
import com.shbvn.jms.dto.response.RolePermissionResponse;
import com.shbvn.jms.model.Permission;
import com.shbvn.jms.security.CustomUserDetails;
import com.shbvn.jms.service.PermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/workspaces/{workspaceId}/role-permissions")
@RequiredArgsConstructor
public class RolePermissionController {

    private final PermissionService permissionService;

    @GetMapping
    @PreAuthorize("@perm.check(#workspaceId, 'workspace:manage_settings')")
    public ResponseEntity<List<PermissionResponse>> getAllPermissions(
            @PathVariable String workspaceId) {
        List<Permission> permissions = permissionService.getAllPermissions();
        List<PermissionResponse> response = permissions.stream()
                .map(p -> new PermissionResponse(p.getId(), p.getName(),
                        p.getDescription(), p.getScope().name()))
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{role}")
    @PreAuthorize("@perm.check(#workspaceId, 'workspace:manage_settings')")
    public ResponseEntity<RolePermissionResponse> getRolePermissions(
            @PathVariable String workspaceId,
            @PathVariable String role) {
        Set<String> permissions = permissionService.getPermissionsForRole(role, workspaceId);
        return ResponseEntity.ok(new RolePermissionResponse(role, new ArrayList<>(permissions)));
    }

    @PutMapping("/{role}")
    @PreAuthorize("@perm.check(#workspaceId, 'workspace:manage_settings')")
    public ResponseEntity<RolePermissionResponse> updateRolePermissions(
            @PathVariable String workspaceId,
            @PathVariable String role,
            @Valid @RequestBody UpdateRolePermissionsRequest request) {
        permissionService.updateRolePermissions(workspaceId, role, request.getPermissionNames());
        Set<String> updated = permissionService.getPermissionsForRole(role, workspaceId);
        return ResponseEntity.ok(new RolePermissionResponse(role, new ArrayList<>(updated)));
    }

    @GetMapping("/me")
    @PreAuthorize("@perm.isMember(#workspaceId)")
    public ResponseEntity<List<String>> getMyPermissions(
            @PathVariable String workspaceId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<String> permissions = permissionService.getUserPermissions(
                userDetails.getId(), workspaceId);
        return ResponseEntity.ok(permissions);
    }
}
