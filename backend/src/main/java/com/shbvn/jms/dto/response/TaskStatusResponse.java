package com.shbvn.jms.dto.response;

import java.time.LocalDateTime;

public record TaskStatusResponse(
    String id,
    String workspaceId,
    String name,
    String slug,
    String color,
    String category,
    Integer position,
    Boolean isDefault,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
