package com.yfshop.admin.api.order.result;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class StOrderResult {

    private DataDTO data;
    private Boolean success;
    private String errorCode;
    private String errorMsg;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        private String expressCode;
        private String waybillCode;
        private String platformOrderId;
    }
}