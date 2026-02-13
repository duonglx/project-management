package com.shbvn.jms.dto.request;

import com.shbvn.jms.model.enums.Priority;
import com.shbvn.jms.model.enums.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProjectRequest {

    private String name;

    private String description;

    private Priority priority;

    private ProjectStatus status;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String teamLead;

    private Integer progress;
}
