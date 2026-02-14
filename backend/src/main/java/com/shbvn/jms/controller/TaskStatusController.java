package com.shbvn.jms.controller;

import com.shbvn.jms.dto.request.CreateTaskStatusRequest;
import com.shbvn.jms.dto.request.ReorderStatusesRequest;
import com.shbvn.jms.dto.request.UpdateTaskStatusRequest;
import com.shbvn.jms.dto.response.TaskStatusResponse;
import com.shbvn.jms.model.TaskStatusEntity;
import com.shbvn.jms.service.TaskStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workspaces/{workspaceId}/task-statuses")
@RequiredArgsConstructor
public class TaskStatusController {

    private final TaskStatusService taskStatusService;

    @GetMapping
    @PreAuthorize("@perm.isMember(#workspaceId)")
    public ResponseEntity<List<TaskStatusResponse>> getStatuses(@PathVariable String workspaceId) {
        List<TaskStatusEntity> statuses = taskStatusService.getStatusesByWorkspaceId(workspaceId);
        return ResponseEntity.ok(statuses.stream().map(this::toResponse).toList());
    }

    @PostMapping
    @PreAuthorize("@perm.check(#workspaceId, 'workspace:manage_settings')")
    public ResponseEntity<TaskStatusResponse> createStatus(
            @PathVariable String workspaceId,
            @Valid @RequestBody CreateTaskStatusRequest request) {
        TaskStatusEntity entity = TaskStatusEntity.builder()
                .name(request.getName())
                .color(request.getColor())
                .category(request.getCategory())
                .isDefault(request.getIsDefault())
                .build();
        TaskStatusEntity created = taskStatusService.createStatus(workspaceId, entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    @PutMapping("/{statusId}")
    @PreAuthorize("@perm.check(#workspaceId, 'workspace:manage_settings')")
    public ResponseEntity<TaskStatusResponse> updateStatus(
            @PathVariable String workspaceId,
            @PathVariable String statusId,
            @Valid @RequestBody UpdateTaskStatusRequest request) {
        TaskStatusEntity updates = TaskStatusEntity.builder()
                .name(request.getName())
                .color(request.getColor())
                .category(request.getCategory())
                .isDefault(request.getIsDefault())
                .build();
        TaskStatusEntity updated = taskStatusService.updateStatus(workspaceId, statusId, updates);
        return ResponseEntity.ok(toResponse(updated));
    }

    @DeleteMapping("/{statusId}")
    @PreAuthorize("@perm.check(#workspaceId, 'workspace:manage_settings')")
    public ResponseEntity<Void> deleteStatus(
            @PathVariable String workspaceId,
            @PathVariable String statusId) {
        taskStatusService.deleteStatus(workspaceId, statusId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/reorder")
    @PreAuthorize("@perm.check(#workspaceId, 'workspace:manage_settings')")
    public ResponseEntity<Void> reorderStatuses(
            @PathVariable String workspaceId,
            @Valid @RequestBody ReorderStatusesRequest request) {
        List<TaskStatusEntity> items = request.getItems().stream()
                .map(i -> TaskStatusEntity.builder().id(i.getId()).position(i.getPosition()).build())
                .toList();
        taskStatusService.reorderStatuses(workspaceId, items);
        return ResponseEntity.ok().build();
    }

    private TaskStatusResponse toResponse(TaskStatusEntity entity) {
        return new TaskStatusResponse(
                entity.getId(), entity.getWorkspaceId(), entity.getName(), entity.getSlug(),
                entity.getColor(), entity.getCategory().name(), entity.getPosition(),
                entity.getIsDefault(), entity.getCreatedAt(), entity.getUpdatedAt()
        );
    }
}
