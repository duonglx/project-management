package com.shbvn.jms.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class UpdateCustomFieldValuesRequest {
    private List<FieldValueItem> values;

    @Data
    public static class FieldValueItem {
        private String fieldId;
        private String value;
    }
}
