package com.shbvn.jms.dto.response;

import java.util.List;

public record RolePermissionResponse(
    String role,
    List<String> permissions
) {}
