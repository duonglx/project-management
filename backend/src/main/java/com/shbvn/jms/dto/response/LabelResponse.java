package com.shbvn.jms.dto.response;

import java.time.LocalDateTime;

public record LabelResponse(
    String id,
    String workspaceId,
    String name,
    String color,
    String description,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
