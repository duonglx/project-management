package com.shbvn.jms.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class UpdateRolePermissionsRequest {
    @NotEmpty(message = "Permission names required")
    private List<String> permissionNames;
}
