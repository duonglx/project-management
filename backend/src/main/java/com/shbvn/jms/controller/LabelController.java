package com.shbvn.jms.controller;

import com.shbvn.jms.dto.request.CreateLabelRequest;
import com.shbvn.jms.dto.request.UpdateLabelRequest;
import com.shbvn.jms.dto.response.LabelResponse;
import com.shbvn.jms.model.Label;
import com.shbvn.jms.service.LabelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workspaces/{workspaceId}/labels")
@RequiredArgsConstructor
public class LabelController {

    private final LabelService labelService;

    @GetMapping
    @PreAuthorize("@perm.isMember(#workspaceId)")
    public ResponseEntity<List<LabelResponse>> getLabels(@PathVariable String workspaceId) {
        List<Label> labels = labelService.getLabelsByWorkspaceId(workspaceId);
        List<LabelResponse> response = labels.stream().map(this::toResponse).toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("@perm.check(#workspaceId, 'workspace:manage_settings')")
    public ResponseEntity<LabelResponse> createLabel(
            @PathVariable String workspaceId,
            @Valid @RequestBody CreateLabelRequest request) {
        Label label = Label.builder()
                .name(request.getName())
                .color(request.getColor())
                .description(request.getDescription())
                .build();
        Label created = labelService.createLabel(workspaceId, label);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    @PutMapping("/{labelId}")
    @PreAuthorize("@perm.check(#workspaceId, 'workspace:manage_settings')")
    public ResponseEntity<LabelResponse> updateLabel(
            @PathVariable String workspaceId,
            @PathVariable String labelId,
            @Valid @RequestBody UpdateLabelRequest request) {
        Label updates = Label.builder()
                .name(request.getName())
                .color(request.getColor())
                .description(request.getDescription())
                .build();
        Label updated = labelService.updateLabel(workspaceId, labelId, updates);
        return ResponseEntity.ok(toResponse(updated));
    }

    @DeleteMapping("/{labelId}")
    @PreAuthorize("@perm.check(#workspaceId, 'workspace:manage_settings')")
    public ResponseEntity<Void> deleteLabel(
            @PathVariable String workspaceId,
            @PathVariable String labelId) {
        labelService.deleteLabel(workspaceId, labelId);
        return ResponseEntity.noContent().build();
    }

    private LabelResponse toResponse(Label label) {
        return new LabelResponse(
                label.getId(), label.getWorkspaceId(), label.getName(),
                label.getColor(), label.getDescription(),
                label.getCreatedAt(), label.getUpdatedAt()
        );
    }
}
