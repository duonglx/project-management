package com.shbvn.jms.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class ReorderCustomFieldsRequest {
    private List<ReorderItem> items;

    @Data
    public static class ReorderItem {
        private String id;
        private int position;
    }
}
