package com.shbvn.jms.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReorderStatusesRequest {

    @NotEmpty(message = "Items list is required")
    private List<ReorderItem> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReorderItem {
        private String id;
        private Integer position;
    }
}
