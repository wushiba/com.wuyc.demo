package com.yfshop.open.api.blpshop.result;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CheckRefundStatusResult implements Serializable {
    private String code;
    private String message;
    private String subMessage;
    private String refundStatus;
    private String refundStatusDescription;
    private List<ChildrenRefundStatus> childrenRefundStatus;

    @Data
    public static class ChildrenRefundStatus implements Serializable {
        private String suborderNo;
        private String refundNo;
        private String productName;
        private String refundStatus;
        private String refundStatusDescription;
    }
}
