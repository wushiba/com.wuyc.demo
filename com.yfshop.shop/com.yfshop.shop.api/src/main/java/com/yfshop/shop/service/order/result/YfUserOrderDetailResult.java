package com.yfshop.shop.service.order.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 返回给商城前台用户订单详情展示的通用返回值
 *
 * @author wuyc
 * created 2021/3/31 15:59
 **/
@Data
public class YfUserOrderDetailResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 下单时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createTime;

    private Long orderId;

    private Long orderDetailId;

    /**
     * 商品数量
     */
    private Integer itemCount;

    /**
     * 订单状态
     */
    private String orderStatus;

    /**
     * 订单总金额
     */
    private BigDecimal orderPrice;

    /**
     * 优惠金额
     */
    private BigDecimal couponPrice;

    /**
     * 运费
     */
    private BigDecimal freight;

    /**
     * 实际付款金额
     */
    private BigDecimal payPrice;

    /**
     * 是否支付 Y | N,
     */
    private String isPay;

    /**
     * 快递单号
     */
    private String expressNo;

    private List<YfUserOrderItem> itemList;

    private YfUserOrderAddress addressInfo;

    @Data
    public static class YfUserOrderItem implements Serializable {

        private static final long serialVersionUID = 1L;

        private Integer skuId;

        private Integer itemId;

        /**
         * 商品数量
         */
        private Integer itemCount;

        private String itemTitle;

        private BigDecimal itemPrice;

        private String itemCover;

        private String specNameValueJson;

        private String specValueStr;
    }

    @Data
    public static class YfUserOrderAddress implements Serializable {

        private static final long serialVersionUID = 1L;

        private String realname;

        private String mobile;

        private String province;

        private String city;

        private String district;

        private String address;
    }

}
