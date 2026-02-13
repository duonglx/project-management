package com.shbvn.jms.repository;

import com.shbvn.jms.model.Permission;
import com.shbvn.jms.model.enums.PermissionScope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {
    List<Permission> findByScope(PermissionScope scope);
    Optional<Permission> findByName(String name);
}
