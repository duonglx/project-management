package com.shbvn.jms.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record WorkspaceResponse(
    String id,
    String name,
    String slug,
    String description,
    Map<String, Object> settings,
    String ownerId,
    String imageUrl,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    List<WorkspaceMemberResponse> members,
    List<ProjectResponse> projects,
    UserResponse owner
) {}
