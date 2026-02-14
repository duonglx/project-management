package com.shbvn.jms.controller;

import com.shbvn.jms.dto.mapper.TaskMapper;
import com.shbvn.jms.dto.request.BatchDeleteRequest;
import com.shbvn.jms.dto.request.CreateTaskRequest;
import com.shbvn.jms.dto.request.UpdateTaskRequest;
import com.shbvn.jms.dto.response.TaskResponse;
import com.shbvn.jms.model.Task;
import com.shbvn.jms.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workspaces/{workspaceId}/projects/{projectId}/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final TaskMapper taskMapper;

    @GetMapping
    @PreAuthorize("@perm.isMember(#workspaceId)")
    public ResponseEntity<Page<TaskResponse>> getTasks(
            @PathVariable String workspaceId,
            @PathVariable String projectId,
            Pageable pageable) {
        Page<Task> tasks = taskService.getTasksByProjectId(projectId, pageable);
        return ResponseEntity.ok(tasks.map(taskMapper::toResponse));
    }

    @PostMapping
    @PreAuthorize("@perm.checkProject(#workspaceId, 'task:create', #projectId)")
    public ResponseEntity<TaskResponse> createTask(
            @PathVariable String workspaceId,
            @PathVariable String projectId,
            @Valid @RequestBody CreateTaskRequest request) {
        Task task = Task.builder()
                .projectId(projectId)
                .title(request.getTitle())
                .description(request.getDescription())
                .statusId(request.getStatusId())
                .type(request.getType())
                .priority(request.getPriority())
                .assigneeId(request.getAssigneeId())
                .dueDate(request.getDueDate() != null ? request.getDueDate().toLocalDate() : null)
                .build();

        Task created = taskService.createTask(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskMapper.toResponse(created));
    }

    @GetMapping("/{taskId}")
    @PreAuthorize("@perm.isMember(#workspaceId)")
    public ResponseEntity<TaskResponse> getTaskById(
            @PathVariable String workspaceId,
            @PathVariable String projectId,
            @PathVariable String taskId) {
        Task task = taskService.getTaskById(taskId);
        return ResponseEntity.ok(taskMapper.toResponse(task));
    }

    @PutMapping("/{taskId}")
    @PreAuthorize("@perm.checkProject(#workspaceId, 'task:update', #projectId)")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable String workspaceId,
            @PathVariable String projectId,
            @PathVariable String taskId,
            @Valid @RequestBody UpdateTaskRequest request) {
        Task updates = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .statusId(request.getStatusId())
                .type(request.getType())
                .priority(request.getPriority())
                .assigneeId(request.getAssigneeId())
                .dueDate(request.getDueDate() != null ? request.getDueDate().toLocalDate() : null)
                .build();

        Task updated = taskService.updateTask(taskId, updates);
        return ResponseEntity.ok(taskMapper.toResponse(updated));
    }

    @DeleteMapping("/{taskId}")
    @PreAuthorize("@perm.checkProject(#workspaceId, 'task:delete', #projectId)")
    public ResponseEntity<Void> deleteTask(
            @PathVariable String workspaceId,
            @PathVariable String projectId,
            @PathVariable String taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/batch")
    @PreAuthorize("@perm.checkProject(#workspaceId, 'task:delete', #projectId)")
    public ResponseEntity<Void> batchDeleteTasks(
            @PathVariable String workspaceId,
            @PathVariable String projectId,
            @Valid @RequestBody BatchDeleteRequest request) {
        taskService.deleteTasks(request.getIds());
        return ResponseEntity.noContent().build();
    }
}
