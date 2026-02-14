package com.shbvn.jms.service;

import com.shbvn.jms.exception.ResourceNotFoundException;
import com.shbvn.jms.model.Label;
import com.shbvn.jms.repository.LabelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LabelService {

    private static final int MAX_LABELS_PER_WORKSPACE = 50;

    private final LabelRepository labelRepository;

    public LabelService(LabelRepository labelRepository) {
        this.labelRepository = labelRepository;
    }

    @Transactional(readOnly = true)
    public List<Label> getLabelsByWorkspaceId(String workspaceId) {
        return labelRepository.findByWorkspaceIdOrderByNameAsc(workspaceId);
    }

    @Transactional
    public Label createLabel(String workspaceId, Label label) {
        if (labelRepository.countByWorkspaceId(workspaceId) >= MAX_LABELS_PER_WORKSPACE) {
            throw new IllegalArgumentException("Maximum of " + MAX_LABELS_PER_WORKSPACE + " labels per workspace reached");
        }
        if (labelRepository.existsByWorkspaceIdAndNameIgnoreCase(workspaceId, label.getName())) {
            throw new IllegalArgumentException("A label with this name already exists in this workspace");
        }
        label.setWorkspaceId(workspaceId);
        return labelRepository.save(label);
    }

    @Transactional
    public Label updateLabel(String workspaceId, String labelId, Label updates) {
        Label existing = labelRepository.findById(labelId)
                .orElseThrow(() -> new ResourceNotFoundException("Label", "id", labelId));

        if (!existing.getWorkspaceId().equals(workspaceId)) {
            throw new ResourceNotFoundException("Label", "id", labelId);
        }

        if (updates.getName() != null) {
            if (labelRepository.existsByWorkspaceIdAndNameIgnoreCaseAndIdNot(workspaceId, updates.getName(), labelId)) {
                throw new IllegalArgumentException("A label with this name already exists in this workspace");
            }
            existing.setName(updates.getName());
        }
        if (updates.getColor() != null) {
            existing.setColor(updates.getColor());
        }
        if (updates.getDescription() != null) {
            existing.setDescription(updates.getDescription());
        }

        return labelRepository.save(existing);
    }

    @Transactional
    public void deleteLabel(String workspaceId, String labelId) {
        Label existing = labelRepository.findById(labelId)
                .orElseThrow(() -> new ResourceNotFoundException("Label", "id", labelId));

        if (!existing.getWorkspaceId().equals(workspaceId)) {
            throw new ResourceNotFoundException("Label", "id", labelId);
        }

        labelRepository.delete(existing);
    }
}
