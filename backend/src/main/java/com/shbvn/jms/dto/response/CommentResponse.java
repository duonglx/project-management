package com.shbvn.jms.dto.response;

import java.time.LocalDateTime;

public record CommentResponse(
    String id,
    String content,
    String userId,
    String taskId,
    LocalDateTime createdAt,
    UserResponse user
) {}
