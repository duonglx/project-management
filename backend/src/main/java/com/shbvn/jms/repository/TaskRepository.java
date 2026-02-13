package com.shbvn.jms.repository;

import com.shbvn.jms.model.Task;
import com.shbvn.jms.model.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {
    Page<Task> findByProjectId(String projectId, Pageable pageable);
    List<Task> findByAssigneeId(String assigneeId);
    List<Task> findByProjectIdAndStatus(String projectId, TaskStatus status);
}
