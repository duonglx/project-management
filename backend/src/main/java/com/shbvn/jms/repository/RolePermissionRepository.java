package com.shbvn.jms.repository;

import com.shbvn.jms.model.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, String> {
    List<RolePermission> findByRoleAndWorkspaceId(String role, String workspaceId);
    List<RolePermission> findByRoleAndWorkspaceIdIsNull(String role);
    void deleteByRoleAndWorkspaceId(String role, String workspaceId);
}
