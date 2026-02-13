package com.shbvn.jms.repository;

import com.shbvn.jms.model.WorkspaceMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkspaceMemberRepository extends JpaRepository<WorkspaceMember, String> {
    Page<WorkspaceMember> findByWorkspaceId(String workspaceId, Pageable pageable);
    List<WorkspaceMember> findByUserId(String userId);
    Optional<WorkspaceMember> findByUserIdAndWorkspaceId(String userId, String workspaceId);
    void deleteByUserIdAndWorkspaceId(String userId, String workspaceId);
}
