package com.shbvn.jms.dto.request;

import com.shbvn.jms.model.enums.WorkspaceRole;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddMemberRequest {

    @NotBlank(message = "User ID is required")
    private String userId;

    private WorkspaceRole role;

    private String message;
}
