package com.shbvn.jms.dto.request;

import com.shbvn.jms.model.enums.CustomFieldType;
import lombok.Data;

import java.util.List;

@Data
public class UpdateCustomFieldRequest {
    private String name;
    private CustomFieldType type;
    private List<String> options;
    private Boolean isRequired;
}
