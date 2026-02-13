package com.shbvn.jms.dto.response;

import java.time.LocalDateTime;

public record UserResponse(
    String id,
    String name,
    String email,
    String image,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
