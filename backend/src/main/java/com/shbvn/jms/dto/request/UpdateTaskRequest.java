package com.shbvn.jms.dto.request;

import com.shbvn.jms.model.enums.Priority;
import com.shbvn.jms.model.enums.TaskType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTaskRequest {

    private String title;

    private String description;

    private String statusId;

    private TaskType type;

    private Priority priority;

    private String assigneeId;

    private LocalDateTime dueDate;
}
