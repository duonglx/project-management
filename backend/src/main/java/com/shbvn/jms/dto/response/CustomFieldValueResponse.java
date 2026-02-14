package com.shbvn.jms.dto.response;

public record CustomFieldValueResponse(
    String fieldId,
    String fieldName,
    String fieldType,
    String value
) {}
