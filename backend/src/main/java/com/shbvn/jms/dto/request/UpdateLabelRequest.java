package com.shbvn.jms.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLabelRequest {

    private String name;

    private String color;

    private String description;
}
