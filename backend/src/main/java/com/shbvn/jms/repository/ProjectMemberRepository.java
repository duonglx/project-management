package com.shbvn.jms.repository;

import com.shbvn.jms.model.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, String> {
    List<ProjectMember> findByProjectId(String projectId);
    List<ProjectMember> findByUserId(String userId);
    Optional<ProjectMember> findByUserIdAndProjectId(String userId, String projectId);
    void deleteByUserIdAndProjectId(String userId, String projectId);
}
