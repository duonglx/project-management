package com.shbvn.jms.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record ProjectResponse(
    String id,
    String name,
    String description,
    String priority,
    String status,
    LocalDateTime startDate,
    LocalDateTime endDate,
    String teamLead,
    String workspaceId,
    Integer progress,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    List<TaskResponse> tasks,
    List<ProjectMemberResponse> members
) {}
