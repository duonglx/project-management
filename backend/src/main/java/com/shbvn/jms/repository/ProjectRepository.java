package com.shbvn.jms.repository;

import com.shbvn.jms.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {
    Page<Project> findByWorkspaceId(String workspaceId, Pageable pageable);
}
