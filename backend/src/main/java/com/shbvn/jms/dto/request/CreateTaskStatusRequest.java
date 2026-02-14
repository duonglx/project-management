package com.shbvn.jms.dto.request;

import com.shbvn.jms.model.enums.StatusCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTaskStatusRequest {

    @NotBlank(message = "Status name is required")
    private String name;

    private String color;

    @NotNull(message = "Category is required")
    private StatusCategory category;

    private Boolean isDefault;
}
