package com.yfshop.admin.api.order.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDetailResult implements Serializable {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 用户id
     */
    private String userName;

    /**
     * 收货方式 ZT(自提) | PS(配送)
     */
    private String receiveWay;

    private Long id;

    private String orderNo;

    private String address;
    /**
     * 订单发货时间
     */
    private LocalDateTime shipTime;

    /**
     * 快递公司名称
     */
    private String expressCompany;

    /**
     * 快递单号
     */
    private String expressNo;

    private List<OrderDetails> list;

    @Data
    public static class OrderDetails implements Serializable {
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
        private LocalDateTime createTime;

        private String orderNo;
        /**
         * 用户id编号
         */
        private Integer userId;

        /**
         * 用户id编号
         */
        private String userName;

        /**
         * 订单id
         */
        private Long orderId;

        private Integer merchantId;

        /**
         * pid_path
         */
        private String pidPath;

        private String websiteCode;

        /**
         * 收货方式 ZT(自提) | PS(配送)
         */
        private String receiveWay;

        private String isPay;

        /**
         * 商品封面图
         */
        private String itemCover;

        /**
         * 商品sku售价
         */
        private BigDecimal itemPrice;

        /**
         * 购买的商品数量
         */
        private Integer itemCount;

        /**
         * 子订单运费
         */
        private BigDecimal freight;

        /**
         * 优惠金额
         */
        private BigDecimal couponPrice;

        /**
         * 订单总金额
         */
        private BigDecimal orderPrice;

        /**
         * 支付金额
         */
        private BigDecimal payPrice;

        /**
         * 订单状态 DZF(待支付), 待发货(DFH), 待收货(DSH), 已完成(YWC)
         */
        private String orderStatus;

        private String itemTitle;


        /**
         * 商品sku的规格名称值json串
         */
        private String specNameValueJson;

        /**
         * 商品sku的规格值,"/"拼接(黑色/XXl)
         */
        private String specValueStr;

        /**
         * 订单确认收货时间
         */
        private LocalDateTime confirmTime;


    }
}
