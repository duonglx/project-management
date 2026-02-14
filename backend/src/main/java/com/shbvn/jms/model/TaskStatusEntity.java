package com.shbvn.jms.model;

import com.shbvn.jms.model.enums.StatusCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "task_statuses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskStatusEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "workspace_id", nullable = false, length = 36)
    private String workspaceId;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "slug", nullable = false, length = 50)
    private String slug;

    @Column(name = "color", nullable = false, length = 7)
    private String color;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 20)
    private StatusCategory category;

    @Column(name = "position", nullable = false)
    private Integer position;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (id == null) id = UUID.randomUUID().toString();
        if (color == null) color = "#3b82f6";
        if (isDefault == null) isDefault = false;
        if (position == null) position = 0;
        if (slug == null && name != null) {
            slug = name.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
        }
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
