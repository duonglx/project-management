package com.shbvn.jms.dto.request;

import com.shbvn.jms.model.enums.StatusCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTaskStatusRequest {

    private String name;

    private String color;

    private StatusCategory category;

    private Boolean isDefault;
}
