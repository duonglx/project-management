package com.shbvn.jms.service;

import com.shbvn.jms.exception.ResourceNotFoundException;
import com.shbvn.jms.model.TaskStatusEntity;
import com.shbvn.jms.model.enums.StatusCategory;
import com.shbvn.jms.repository.TaskRepository;
import com.shbvn.jms.repository.TaskStatusRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskStatusService {

    private final TaskStatusRepository taskStatusRepository;
    private final TaskRepository taskRepository;

    public TaskStatusService(TaskStatusRepository taskStatusRepository, TaskRepository taskRepository) {
        this.taskStatusRepository = taskStatusRepository;
        this.taskRepository = taskRepository;
    }

    @Transactional(readOnly = true)
    public List<TaskStatusEntity> getStatusesByWorkspaceId(String workspaceId) {
        return taskStatusRepository.findByWorkspaceIdOrderByPositionAsc(workspaceId);
    }

    @Transactional
    public TaskStatusEntity createStatus(String workspaceId, TaskStatusEntity status) {
        status.setWorkspaceId(workspaceId);
        // Auto-set position to end
        long count = taskStatusRepository.countByWorkspaceId(workspaceId);
        status.setPosition((int) count);

        // Handle isDefault — unset others if this is default
        if (Boolean.TRUE.equals(status.getIsDefault())) {
            unsetCurrentDefault(workspaceId);
        }

        return taskStatusRepository.save(status);
    }

    @Transactional
    public TaskStatusEntity updateStatus(String workspaceId, String statusId, TaskStatusEntity updates) {
        TaskStatusEntity existing = findByIdAndWorkspace(statusId, workspaceId);

        if (updates.getName() != null) {
            existing.setName(updates.getName());
            existing.setSlug(updates.getName().toLowerCase()
                    .replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", ""));
        }
        if (updates.getColor() != null) existing.setColor(updates.getColor());
        if (updates.getCategory() != null) existing.setCategory(updates.getCategory());

        if (Boolean.TRUE.equals(updates.getIsDefault())) {
            unsetCurrentDefault(workspaceId);
            existing.setIsDefault(true);
        }

        return taskStatusRepository.save(existing);
    }

    @Transactional
    public void deleteStatus(String workspaceId, String statusId) {
        TaskStatusEntity existing = findByIdAndWorkspace(statusId, workspaceId);

        // Check if tasks reference this status
        long taskCount = taskRepository.countByStatusId(statusId);
        if (taskCount > 0) {
            throw new IllegalArgumentException(
                    "Cannot delete status — " + taskCount + " task(s) still use it. Reassign them first.");
        }

        taskStatusRepository.delete(existing);
    }

    @Transactional
    public void reorderStatuses(String workspaceId, List<TaskStatusEntity> items) {
        for (TaskStatusEntity item : items) {
            TaskStatusEntity existing = findByIdAndWorkspace(item.getId(), workspaceId);
            existing.setPosition(item.getPosition());
            taskStatusRepository.save(existing);
        }
    }

    /** Seed default statuses for a newly created workspace */
    @Transactional
    public void seedDefaults(String workspaceId) {
        if (taskStatusRepository.countByWorkspaceId(workspaceId) > 0) return;

        Object[][] defaults = {
                {"Backlog", "backlog", "#6b7280", StatusCategory.NOT_STARTED, 0, false},
                {"Todo", "todo", "#3b82f6", StatusCategory.NOT_STARTED, 1, true},
                {"In Progress", "in-progress", "#f59e0b", StatusCategory.ACTIVE, 2, false},
                {"In Review", "in-review", "#8b5cf6", StatusCategory.ACTIVE, 3, false},
                {"Done", "done", "#22c55e", StatusCategory.DONE, 4, false},
                {"Cancelled", "cancelled", "#ef4444", StatusCategory.CLOSED, 5, false},
        };
        for (Object[] d : defaults) {
            taskStatusRepository.save(TaskStatusEntity.builder()
                    .workspaceId(workspaceId)
                    .name((String) d[0]).slug((String) d[1]).color((String) d[2])
                    .category((StatusCategory) d[3]).position((Integer) d[4]).isDefault((Boolean) d[5])
                    .build());
        }
    }

    private TaskStatusEntity findByIdAndWorkspace(String id, String workspaceId) {
        TaskStatusEntity entity = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TaskStatus", "id", id));
        if (!entity.getWorkspaceId().equals(workspaceId)) {
            throw new ResourceNotFoundException("TaskStatus", "id", id);
        }
        return entity;
    }

    private void unsetCurrentDefault(String workspaceId) {
        taskStatusRepository.findByWorkspaceIdAndIsDefaultTrue(workspaceId)
                .ifPresent(current -> {
                    current.setIsDefault(false);
                    taskStatusRepository.save(current);
                });
    }
}
