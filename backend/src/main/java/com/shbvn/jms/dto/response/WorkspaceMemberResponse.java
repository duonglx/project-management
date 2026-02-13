package com.shbvn.jms.dto.response;

public record WorkspaceMemberResponse(
    String id,
    String userId,
    String workspaceId,
    String message,
    String role,
    UserResponse user
) {}
