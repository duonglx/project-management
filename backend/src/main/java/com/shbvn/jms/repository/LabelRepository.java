package com.shbvn.jms.repository;

import com.shbvn.jms.model.Label;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LabelRepository extends JpaRepository<Label, String> {

    List<Label> findByWorkspaceIdOrderByNameAsc(String workspaceId);

    long countByWorkspaceId(String workspaceId);

    boolean existsByWorkspaceIdAndNameIgnoreCase(String workspaceId, String name);

    boolean existsByWorkspaceIdAndNameIgnoreCaseAndIdNot(String workspaceId, String name, String id);
}
