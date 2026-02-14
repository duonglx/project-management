package com.shbvn.jms.dto.request;

import com.shbvn.jms.model.enums.Priority;
import com.shbvn.jms.model.enums.TaskType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTaskRequest {

    @NotBlank(message = "Project ID is required")
    private String projectId;

    @NotBlank(message = "Task title is required")
    private String title;

    private String description;

    private String statusId;

    @NotNull(message = "Type is required")
    private TaskType type;

    @NotNull(message = "Priority is required")
    private Priority priority;

    private String assigneeId;

    private LocalDateTime dueDate;
}
