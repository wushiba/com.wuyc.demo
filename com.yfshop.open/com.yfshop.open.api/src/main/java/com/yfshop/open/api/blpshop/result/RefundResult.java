package com.yfshop.open.api.blpshop.result;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RefundResult implements Serializable {
    private Integer totalCount;
    private List<Refunds> refunds;
    private Boolean isSuccess;
    private String message;
    private String code;

    @Data
    public static class Refunds implements Serializable {
        /**
         * 退款单号
         */
        private String refundNo;
        /**
         * 平台订单号
         */
        private String platOrderNo;
        private String subPlatOrderNo;
        private String totalAmount;
        private String payAmount;
        private String buyerNick;
        private String sellerNick;
        private String createTime;
        private String updateTime;
        private String orderStatus;
        private String orderStatusDesc;
        private String refundStatus;
        private String refundStatusDesc;
        private String goodsStatus;
        private String goodsStatusDesc;
        private String hasGoodsReturn;
        private String reason;
        private String desc;
        private Integer productNum;
        private String logisticName;
        private String logisticNo;
        private List<RefundGoods> refundGoods;
    }

    @Data
    public static class RefundGoods implements Serializable {
        private String platProductId;
        private String outerID;
        private String sku;
        private String productName;
        private String refundAmount;
        private String reason;
        private Integer productNum;
        private String poNo;
    }
}
