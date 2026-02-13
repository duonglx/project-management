package com.shbvn.jms.dto.response;

public record ProjectMemberResponse(
    String id,
    String userId,
    String projectId,
    UserResponse user
) {}
