package com.yfshop.admin.api.order.result;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class StOrderResult {

    private Boolean success;
    private String errorCode;
    private String errorMsg;
    private DataDTO data;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        private String orderNo;
        private String waybillNo;
        private String bigWord;
        private String packagePlace;
    }
}
