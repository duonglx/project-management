package com.shbvn.jms.security;

import com.shbvn.jms.model.enums.SystemRole;
import com.shbvn.jms.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Security expression bean for @PreAuthorize SpEL expressions.
 * Referenced as @perm in annotations.
 */
@Component("perm")
@RequiredArgsConstructor
public class PermissionEvaluator {

    private final PermissionService permissionService;

    /** Check if current user has ADMIN_WORKSPACE or SUPER_ADMIN system role. */
    public boolean isAdminWorkspace() {
        CustomUserDetails userDetails = extractCurrentUser();
        if (userDetails == null) return false;
        SystemRole role = userDetails.getSystemRole();
        return role == SystemRole.ADMIN_WORKSPACE || role == SystemRole.SUPER_ADMIN;
    }

    /** Check if current user is a member of the given workspace. */
    public boolean isMember(String workspaceId) {
        CustomUserDetails userDetails = extractCurrentUser();
        if (userDetails == null) return false;
        // Admin_Workspace can access any workspace
        if (isAdminWorkspace()) return true;
        return permissionService.isMember(userDetails.getId(), workspaceId);
    }

    /** Check workspace-level permission. */
    public boolean check(String workspaceId, String permissionName) {
        CustomUserDetails userDetails = extractCurrentUser();
        if (userDetails == null) return false;
        if (isAdminWorkspace()) return true;
        return permissionService.hasPermission(userDetails.getId(), workspaceId, permissionName);
    }

    /** Check project-level permission. */
    public boolean checkProject(String workspaceId, String permissionName, String projectId) {
        CustomUserDetails userDetails = extractCurrentUser();
        if (userDetails == null) return false;
        if (isAdminWorkspace()) return true;
        return permissionService.hasProjectPermission(
                userDetails.getId(), workspaceId, projectId, permissionName);
    }

    private CustomUserDetails extractCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
            return (CustomUserDetails) auth.getPrincipal();
        }
        return null;
    }
}
