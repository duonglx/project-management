package com.shbvn.jms.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateWorkspaceRequest {

    private String name;

    private String description;

    private String imageUrl;

    private Map<String, Object> settings;
}
