package com.shbvn.jms.dto.request;

import com.shbvn.jms.model.enums.Priority;
import com.shbvn.jms.model.enums.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProjectRequest {

    @NotBlank(message = "Project name is required")
    private String name;

    private String description;

    @NotNull(message = "Priority is required")
    private Priority priority;

    @NotNull(message = "Status is required")
    private ProjectStatus status;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @NotBlank(message = "Team lead is required")
    private String teamLead;

    @NotBlank(message = "Workspace ID is required")
    private String workspaceId;

    private Integer progress;
}
