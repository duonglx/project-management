package com.shbvn.jms.dto.response;

public record PermissionResponse(
    String id,
    String name,
    String description,
    String scope
) {}
