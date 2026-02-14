package com.shbvn.jms.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateLabelRequest {

    @NotBlank(message = "Label name is required")
    private String name;

    private String color;

    private String description;
}
