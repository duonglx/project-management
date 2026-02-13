package com.shbvn.jms.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record TaskResponse(
    String id,
    String projectId,
    String title,
    String description,
    String status,
    String type,
    String priority,
    String assigneeId,
    LocalDateTime dueDate,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    UserResponse assignee,
    List<CommentResponse> comments
) {}
