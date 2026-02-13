package com.shbvn.jms.controller;

import com.shbvn.jms.dto.mapper.TaskMapper;
import com.shbvn.jms.dto.request.BatchDeleteRequest;
import com.shbvn.jms.dto.request.CreateTaskRequest;
import com.shbvn.jms.dto.request.UpdateTaskRequest;
import com.shbvn.jms.dto.response.TaskResponse;
import com.shbvn.jms.model.Task;
import com.shbvn.jms.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    private final TaskService taskService;
    private final TaskMapper taskMapper;

    public TaskController(TaskService taskService, TaskMapper taskMapper) {
        this.taskService = taskService;
        this.taskMapper = taskMapper;
    }

    @GetMapping
    public ResponseEntity<Page<TaskResponse>> getTasks(
            @RequestParam String projectId,
            Pageable pageable) {
        Page<Task> tasks = taskService.getTasksByProjectId(projectId, pageable);
        Page<TaskResponse> response = tasks.map(taskMapper::toResponse);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody CreateTaskRequest request) {
        Task task = Task.builder()
                .projectId(request.getProjectId())
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus())
                .type(request.getType())
                .priority(request.getPriority())
                .assigneeId(request.getAssigneeId())
                .dueDate(request.getDueDate() != null ? request.getDueDate().toLocalDate() : null)
                .build();

        Task created = taskService.createTask(task);
        TaskResponse response = taskMapper.toResponse(created);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable String id) {
        Task task = taskService.getTaskById(id);
        TaskResponse response = taskMapper.toResponse(task);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable String id,
            @Valid @RequestBody UpdateTaskRequest request) {
        Task updates = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus())
                .type(request.getType())
                .priority(request.getPriority())
                .assigneeId(request.getAssigneeId())
                .dueDate(request.getDueDate() != null ? request.getDueDate().toLocalDate() : null)
                .build();

        Task updated = taskService.updateTask(id, updates);
        TaskResponse response = taskMapper.toResponse(updated);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable String id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/batch")
    public ResponseEntity<Void> batchDeleteTasks(@Valid @RequestBody BatchDeleteRequest request) {
        taskService.deleteTasks(request.getIds());
        return ResponseEntity.noContent().build();
    }
}
