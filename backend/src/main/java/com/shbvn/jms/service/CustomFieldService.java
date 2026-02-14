package com.shbvn.jms.service;

import com.shbvn.jms.dto.request.ReorderCustomFieldsRequest;
import com.shbvn.jms.dto.request.UpdateCustomFieldValuesRequest;
import com.shbvn.jms.exception.ResourceNotFoundException;
import com.shbvn.jms.model.CustomFieldDefinition;
import com.shbvn.jms.model.CustomFieldValue;
import com.shbvn.jms.model.enums.CustomFieldType;
import com.shbvn.jms.repository.CustomFieldDefinitionRepository;
import com.shbvn.jms.repository.CustomFieldValueRepository;
import com.shbvn.jms.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@Service
public class CustomFieldService {

    private final CustomFieldDefinitionRepository definitionRepo;
    private final CustomFieldValueRepository valueRepo;
    private final TaskRepository taskRepo;

    public CustomFieldService(CustomFieldDefinitionRepository definitionRepo,
                              CustomFieldValueRepository valueRepo,
                              TaskRepository taskRepo) {
        this.definitionRepo = definitionRepo;
        this.valueRepo = valueRepo;
        this.taskRepo = taskRepo;
    }

    @Transactional(readOnly = true)
    public List<CustomFieldDefinition> getDefinitions(String workspaceId) {
        return definitionRepo.findByWorkspaceIdOrderByPositionAsc(workspaceId);
    }

    @Transactional
    public CustomFieldDefinition createDefinition(String workspaceId, CustomFieldDefinition def) {
        if (definitionRepo.countByWorkspaceId(workspaceId) >= 20) {
            throw new IllegalArgumentException("Maximum 20 custom fields per workspace");
        }
        if (definitionRepo.existsByWorkspaceIdAndNameIgnoreCase(workspaceId, def.getName())) {
            throw new IllegalArgumentException("Field name already exists in this workspace");
        }
        if (def.getType() == CustomFieldType.DROPDOWN && (def.getOptions() == null || def.getOptions().isEmpty())) {
            throw new IllegalArgumentException("DROPDOWN fields require at least one option");
        }
        def.setWorkspaceId(workspaceId);
        // Set position to end
        long count = definitionRepo.countByWorkspaceId(workspaceId);
        def.setPosition((int) count);
        return definitionRepo.save(def);
    }

    @Transactional
    public CustomFieldDefinition updateDefinition(String workspaceId, String fieldId, CustomFieldDefinition updates) {
        CustomFieldDefinition existing = definitionRepo.findById(fieldId)
                .orElseThrow(() -> new ResourceNotFoundException("CustomField", "id", fieldId));
        if (!existing.getWorkspaceId().equals(workspaceId)) {
            throw new ResourceNotFoundException("CustomField", "id", fieldId);
        }
        if (updates.getName() != null) {
            if (definitionRepo.existsByWorkspaceIdAndNameIgnoreCaseAndIdNot(workspaceId, updates.getName(), fieldId)) {
                throw new IllegalArgumentException("Field name already exists in this workspace");
            }
            existing.setName(updates.getName());
        }
        if (updates.getType() != null) {
            existing.setType(updates.getType());
        }
        if (updates.getOptions() != null) {
            existing.setOptions(updates.getOptions());
        }
        if (updates.isRequired() != existing.isRequired()) {
            existing.setRequired(updates.isRequired());
        }
        return definitionRepo.save(existing);
    }

    @Transactional
    public void deleteDefinition(String workspaceId, String fieldId) {
        CustomFieldDefinition existing = definitionRepo.findById(fieldId)
                .orElseThrow(() -> new ResourceNotFoundException("CustomField", "id", fieldId));
        if (!existing.getWorkspaceId().equals(workspaceId)) {
            throw new ResourceNotFoundException("CustomField", "id", fieldId);
        }
        // Values cascade-deleted by DB FK
        definitionRepo.delete(existing);
    }

    @Transactional
    public void reorderDefinitions(String workspaceId, ReorderCustomFieldsRequest request) {
        for (ReorderCustomFieldsRequest.ReorderItem item : request.getItems()) {
            CustomFieldDefinition def = definitionRepo.findById(item.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("CustomField", "id", item.getId()));
            if (!def.getWorkspaceId().equals(workspaceId)) {
                throw new ResourceNotFoundException("CustomField", "id", item.getId());
            }
            def.setPosition(item.getPosition());
            definitionRepo.save(def);
        }
    }

    @Transactional(readOnly = true)
    public List<CustomFieldValue> getValuesForTask(String taskId) {
        if (!taskRepo.existsById(taskId)) {
            throw new ResourceNotFoundException("Task", "id", taskId);
        }
        return valueRepo.findByTaskId(taskId);
    }

    @Transactional
    public List<CustomFieldValue> updateValuesForTask(String taskId, UpdateCustomFieldValuesRequest request) {
        if (!taskRepo.existsById(taskId)) {
            throw new ResourceNotFoundException("Task", "id", taskId);
        }
        for (UpdateCustomFieldValuesRequest.FieldValueItem item : request.getValues()) {
            CustomFieldDefinition def = definitionRepo.findById(item.getFieldId())
                    .orElseThrow(() -> new ResourceNotFoundException("CustomField", "id", item.getFieldId()));
            validateValue(def, item.getValue());

            CustomFieldValue value = valueRepo.findByTaskIdAndFieldId(taskId, item.getFieldId())
                    .orElse(CustomFieldValue.builder().taskId(taskId).fieldId(item.getFieldId()).build());
            value.setValue(item.getValue());
            valueRepo.save(value);
        }
        return valueRepo.findByTaskId(taskId);
    }

    private void validateValue(CustomFieldDefinition def, String value) {
        if (value == null || value.isBlank()) return;
        switch (def.getType()) {
            case TEXT -> {
                if (value.length() > 500) throw new IllegalArgumentException("TEXT value max 500 chars");
            }
            case NUMBER -> {
                try { Double.parseDouble(value); }
                catch (NumberFormatException e) { throw new IllegalArgumentException("Invalid number value"); }
            }
            case DROPDOWN -> {
                if (def.getOptions() == null || !def.getOptions().contains(value)) {
                    throw new IllegalArgumentException("Value not in dropdown options");
                }
            }
            case DATE -> {
                if (!value.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    throw new IllegalArgumentException("Date must be yyyy-MM-dd format");
                }
            }
            case CHECKBOX -> {
                if (!"true".equals(value) && !"false".equals(value)) {
                    throw new IllegalArgumentException("Checkbox value must be 'true' or 'false'");
                }
            }
            case URL -> {
                if (value.length() > 2000) throw new IllegalArgumentException("URL value max 2000 chars");
                try { new URL(value); }
                catch (MalformedURLException e) { throw new IllegalArgumentException("Invalid URL format"); }
            }
        }
    }
}
