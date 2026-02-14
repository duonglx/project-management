package com.shbvn.jms.repository;

import com.shbvn.jms.model.CustomFieldDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomFieldDefinitionRepository extends JpaRepository<CustomFieldDefinition, String> {
    List<CustomFieldDefinition> findByWorkspaceIdOrderByPositionAsc(String workspaceId);
    long countByWorkspaceId(String workspaceId);
    boolean existsByWorkspaceIdAndNameIgnoreCase(String workspaceId, String name);
    boolean existsByWorkspaceIdAndNameIgnoreCaseAndIdNot(String workspaceId, String name, String id);
}
