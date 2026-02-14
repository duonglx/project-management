package com.shbvn.jms.repository;

import com.shbvn.jms.model.TaskStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskStatusRepository extends JpaRepository<TaskStatusEntity, String> {

    List<TaskStatusEntity> findByWorkspaceIdOrderByPositionAsc(String workspaceId);

    Optional<TaskStatusEntity> findByWorkspaceIdAndIsDefaultTrue(String workspaceId);

    boolean existsByIdAndWorkspaceId(String id, String workspaceId);

    long countByWorkspaceId(String workspaceId);

    boolean existsByWorkspaceIdAndSlug(String workspaceId, String slug);
}
