package com.shbvn.jms.dto.request;

import com.shbvn.jms.model.enums.CustomFieldType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateCustomFieldRequest {
    @NotBlank
    private String name;

    @NotNull
    private CustomFieldType type;

    private List<String> options;

    private boolean isRequired;
}
