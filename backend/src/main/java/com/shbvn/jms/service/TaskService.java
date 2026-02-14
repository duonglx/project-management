package com.shbvn.jms.service;

import com.shbvn.jms.exception.ResourceNotFoundException;
import com.shbvn.jms.model.Task;
import com.shbvn.jms.repository.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Transactional(readOnly = true)
    public Page<Task> getTasksByProjectId(String projectId, Pageable pageable) {
        Page<Task> tasks = taskRepository.findByProjectId(projectId, pageable);
        // Clear assignees and comments to avoid lazy loading issues in list view
        tasks.forEach(task -> {
            task.setAssignee(null);
            task.setComments(null);
        });
        return tasks;
    }

    @Transactional(readOnly = true)
    public Task getTaskById(String id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));

        // Trigger lazy loading for comments, assignee, and task status
        task.getComments().size();
        if (task.getAssigneeId() != null) {
            task.getAssignee().getName();
        }
        if (task.getTaskStatus() != null) {
            task.getTaskStatus().getName();
        }

        return task;
    }

    public List<Task> getTasksByAssigneeId(String assigneeId) {
        return taskRepository.findByAssigneeId(assigneeId);
    }

    @Transactional
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    @Transactional
    public Task updateTask(String id, Task updates) {
        Task existing = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));

        if (updates.getTitle() != null) {
            existing.setTitle(updates.getTitle());
        }
        if (updates.getDescription() != null) {
            existing.setDescription(updates.getDescription());
        }
        if (updates.getStatusId() != null) {
            existing.setStatusId(updates.getStatusId());
        }
        if (updates.getType() != null) {
            existing.setType(updates.getType());
        }
        if (updates.getPriority() != null) {
            existing.setPriority(updates.getPriority());
        }
        if (updates.getDueDate() != null) {
            existing.setDueDate(updates.getDueDate());
        }
        if (updates.getAssigneeId() != null) {
            existing.setAssigneeId(updates.getAssigneeId());
        }

        Task saved = taskRepository.save(existing);

        // Trigger lazy loading for comments, assignee, and task status
        saved.getComments().size();
        if (saved.getAssigneeId() != null) {
            saved.getAssignee().getName();
        }
        if (saved.getTaskStatus() != null) {
            saved.getTaskStatus().getName();
        }

        return saved;
    }

    @Transactional
    public void deleteTask(String id) {
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("Task", "id", id);
        }
        taskRepository.deleteById(id);
    }

    @Transactional
    public void deleteTasks(List<String> ids) {
        taskRepository.deleteAllById(ids);
    }
}
