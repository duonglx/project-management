package com.shbvn.jms.controller;

import com.shbvn.jms.dto.request.CreateCustomFieldRequest;
import com.shbvn.jms.dto.request.ReorderCustomFieldsRequest;
import com.shbvn.jms.dto.request.UpdateCustomFieldRequest;
import com.shbvn.jms.dto.request.UpdateCustomFieldValuesRequest;
import com.shbvn.jms.dto.response.CustomFieldDefinitionResponse;
import com.shbvn.jms.dto.response.CustomFieldValueResponse;
import com.shbvn.jms.model.CustomFieldDefinition;
import com.shbvn.jms.model.CustomFieldValue;
import com.shbvn.jms.service.CustomFieldService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CustomFieldController {

    private final CustomFieldService customFieldService;

    // --- Definition CRUD (workspace-scoped) ---

    @GetMapping("/api/workspaces/{workspaceId}/custom-fields")
    @PreAuthorize("@perm.isMember(#workspaceId)")
    public ResponseEntity<List<CustomFieldDefinitionResponse>> getDefinitions(@PathVariable String workspaceId) {
        List<CustomFieldDefinition> defs = customFieldService.getDefinitions(workspaceId);
        return ResponseEntity.ok(defs.stream().map(this::toDefinitionResponse).toList());
    }

    @PostMapping("/api/workspaces/{workspaceId}/custom-fields")
    @PreAuthorize("@perm.check(#workspaceId, 'workspace:manage_settings')")
    public ResponseEntity<CustomFieldDefinitionResponse> createDefinition(
            @PathVariable String workspaceId,
            @Valid @RequestBody CreateCustomFieldRequest request) {
        CustomFieldDefinition def = CustomFieldDefinition.builder()
                .name(request.getName())
                .type(request.getType())
                .options(request.getOptions())
                .isRequired(request.isRequired())
                .build();
        CustomFieldDefinition created = customFieldService.createDefinition(workspaceId, def);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDefinitionResponse(created));
    }

    @PutMapping("/api/workspaces/{workspaceId}/custom-fields/{fieldId}")
    @PreAuthorize("@perm.check(#workspaceId, 'workspace:manage_settings')")
    public ResponseEntity<CustomFieldDefinitionResponse> updateDefinition(
            @PathVariable String workspaceId,
            @PathVariable String fieldId,
            @Valid @RequestBody UpdateCustomFieldRequest request) {
        CustomFieldDefinition updates = CustomFieldDefinition.builder()
                .name(request.getName())
                .type(request.getType())
                .options(request.getOptions())
                .isRequired(request.getIsRequired() != null && request.getIsRequired())
                .build();
        CustomFieldDefinition updated = customFieldService.updateDefinition(workspaceId, fieldId, updates);
        return ResponseEntity.ok(toDefinitionResponse(updated));
    }

    @DeleteMapping("/api/workspaces/{workspaceId}/custom-fields/{fieldId}")
    @PreAuthorize("@perm.check(#workspaceId, 'workspace:manage_settings')")
    public ResponseEntity<Void> deleteDefinition(
            @PathVariable String workspaceId,
            @PathVariable String fieldId) {
        customFieldService.deleteDefinition(workspaceId, fieldId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/api/workspaces/{workspaceId}/custom-fields/reorder")
    @PreAuthorize("@perm.check(#workspaceId, 'workspace:manage_settings')")
    public ResponseEntity<Void> reorderDefinitions(
            @PathVariable String workspaceId,
            @RequestBody ReorderCustomFieldsRequest request) {
        customFieldService.reorderDefinitions(workspaceId, request);
        return ResponseEntity.ok().build();
    }

    // --- Value CRUD (task-scoped) ---

    @GetMapping("/api/tasks/{taskId}/custom-fields")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CustomFieldValueResponse>> getTaskValues(@PathVariable String taskId) {
        List<CustomFieldValue> values = customFieldService.getValuesForTask(taskId);
        return ResponseEntity.ok(values.stream().map(this::toValueResponse).toList());
    }

    @PutMapping("/api/tasks/{taskId}/custom-fields")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CustomFieldValueResponse>> updateTaskValues(
            @PathVariable String taskId,
            @RequestBody UpdateCustomFieldValuesRequest request) {
        List<CustomFieldValue> values = customFieldService.updateValuesForTask(taskId, request);
        return ResponseEntity.ok(values.stream().map(this::toValueResponse).toList());
    }

    // --- Mappers ---

    private CustomFieldDefinitionResponse toDefinitionResponse(CustomFieldDefinition def) {
        return new CustomFieldDefinitionResponse(
                def.getId(), def.getWorkspaceId(), def.getName(),
                def.getType().name(), def.getOptions(),
                def.isRequired(), def.getPosition());
    }

    private CustomFieldValueResponse toValueResponse(CustomFieldValue val) {
        String fieldName = val.getDefinition() != null ? val.getDefinition().getName() : null;
        String fieldType = val.getDefinition() != null ? val.getDefinition().getType().name() : null;
        return new CustomFieldValueResponse(val.getFieldId(), fieldName, fieldType, val.getValue());
    }
}
