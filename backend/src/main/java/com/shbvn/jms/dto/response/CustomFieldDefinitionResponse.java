package com.shbvn.jms.dto.response;

import java.util.List;

public record CustomFieldDefinitionResponse(
    String id,
    String workspaceId,
    String name,
    String type,
    List<String> options,
    boolean isRequired,
    int position
) {}
