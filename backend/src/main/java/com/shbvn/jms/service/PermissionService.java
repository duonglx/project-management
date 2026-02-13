package com.shbvn.jms.service;

import com.shbvn.jms.model.Permission;
import com.shbvn.jms.model.RolePermission;
import com.shbvn.jms.model.WorkspaceMember;
import com.shbvn.jms.model.ProjectMember;
import com.shbvn.jms.model.enums.WorkspaceRole;
import com.shbvn.jms.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionRepository permissionRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final ProjectMemberRepository projectMemberRepository;

    /**
     * Get permission names for a given role in a workspace context.
     * Workspace-specific overrides take precedence over system defaults.
     */
    @Cacheable(value = "rolePermissions", key = "#role + ':' + #workspaceId")
    public Set<String> getPermissionsForRole(String role, String workspaceId) {
        // Check workspace-specific overrides first
        List<RolePermission> overrides = rolePermissionRepository
                .findByRoleAndWorkspaceId(role, workspaceId);

        if (!overrides.isEmpty()) {
            return overrides.stream()
                    .map(rp -> rp.getPermission().getName())
                    .collect(Collectors.toSet());
        }

        // Fall back to system defaults
        List<RolePermission> defaults = rolePermissionRepository
                .findByRoleAndWorkspaceIdIsNull(role);
        return defaults.stream()
                .map(rp -> rp.getPermission().getName())
                .collect(Collectors.toSet());
    }

    /**
     * Check if user has a workspace-level permission.
     * OWNER bypasses all checks.
     */
    public boolean hasPermission(String userId, String workspaceId, String permissionName) {
        Optional<WorkspaceMember> membership = workspaceMemberRepository
                .findByUserIdAndWorkspaceId(userId, workspaceId);

        if (membership.isEmpty()) return false;

        WorkspaceRole role = membership.get().getRole();
        if (role == WorkspaceRole.OWNER) return true;

        Set<String> permissions = getPermissionsForRole(role.name(), workspaceId);
        return permissions.contains(permissionName);
    }

    /**
     * Check if user has a project-level permission.
     * Also checks workspace-level permissions (workspace ADMIN has project access).
     */
    public boolean hasProjectPermission(String userId, String workspaceId,
                                         String projectId, String permissionName) {
        // First check if workspace OWNER — bypasses all
        Optional<WorkspaceMember> wsMembership = workspaceMemberRepository
                .findByUserIdAndWorkspaceId(userId, workspaceId);

        if (wsMembership.isPresent() && wsMembership.get().getRole() == WorkspaceRole.OWNER) {
            return true;
        }

        // Check project-level role
        Optional<ProjectMember> projMembership = projectMemberRepository
                .findByUserIdAndProjectId(userId, projectId);

        if (projMembership.isPresent()) {
            String projectRole = projMembership.get().getRole().name();
            Set<String> permissions = getPermissionsForRole(projectRole, workspaceId);
            if (permissions.contains(permissionName)) return true;
        }

        // Also check workspace ADMIN permissions (they have project access)
        if (wsMembership.isPresent()) {
            String wsRole = wsMembership.get().getRole().name();
            Set<String> wsPermissions = getPermissionsForRole(wsRole, workspaceId);
            return wsPermissions.contains(permissionName);
        }

        return false;
    }

    /**
     * Check if user is a member of the workspace (any role).
     */
    public boolean isMember(String userId, String workspaceId) {
        return workspaceMemberRepository.findByUserIdAndWorkspaceId(userId, workspaceId).isPresent();
    }

    /**
     * Get all effective permission names for a user in a workspace.
     */
    public List<String> getUserPermissions(String userId, String workspaceId) {
        Optional<WorkspaceMember> membership = workspaceMemberRepository
                .findByUserIdAndWorkspaceId(userId, workspaceId);

        if (membership.isEmpty()) return Collections.emptyList();

        WorkspaceRole role = membership.get().getRole();
        if (role == WorkspaceRole.OWNER) {
            // OWNER gets all permissions
            return permissionRepository.findAll().stream()
                    .map(Permission::getName)
                    .toList();
        }

        return new ArrayList<>(getPermissionsForRole(role.name(), workspaceId));
    }

    /**
     * Update role permissions for a workspace (admin-only).
     */
    @Transactional
    @CacheEvict(value = "rolePermissions", allEntries = true)
    public void updateRolePermissions(String workspaceId, String role, List<String> permissionNames) {
        rolePermissionRepository.deleteByRoleAndWorkspaceId(role, workspaceId);

        for (String permName : permissionNames) {
            Permission perm = permissionRepository.findByName(permName)
                    .orElseThrow(() -> new IllegalArgumentException("Unknown permission: " + permName));

            RolePermission rp = RolePermission.builder()
                    .role(role)
                    .permission(perm)
                    .workspaceId(workspaceId)
                    .build();
            rolePermissionRepository.save(rp);
        }
    }

    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }
}
