package com.shbvn.jms.dto.request;

import com.shbvn.jms.model.enums.WorkspaceRole;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateMemberRoleRequest {
    @NotNull
    private WorkspaceRole role;
}
