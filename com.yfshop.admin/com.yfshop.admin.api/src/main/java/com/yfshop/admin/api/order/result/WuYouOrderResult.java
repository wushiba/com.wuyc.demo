package com.yfshop.admin.api.order.result;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class WuYouOrderResult {

    private DataDTO data;
    private String success;
    private String errorCode;
    private String errorMsg;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        private String expressName;
        private String expressCode;
        private String waybillCode;
        private String platformOrderId;
    }
}
