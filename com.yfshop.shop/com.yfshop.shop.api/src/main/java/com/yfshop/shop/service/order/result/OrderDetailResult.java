package com.yfshop.shop.service.order.result;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderDetailResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * 用户id编号
     */
    private Integer userId;

    /**
     * 订单id
     */
    private Long orderId;

    private Integer merchantId;

    /**
     * pid_path
     */
    private String pidPath;

    /**
     * 收货方式 ZT(自提) | PS(配送)
     */
    private String receiveWay;

    /**
     * 商品id
     */
    private Integer itemId;

    /**
     * 商品sku编号
     */
    private Integer skuId;

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
     * 订单状态 DZF(待支付), 待发货(DFH), 待收货(DSH), 已完成(YWC)
     */
    private String orderStatus;

    /**
     * 商品sku的specValueIdpath
     */
    private String specValueIdPath;

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

    private OrderAddressResult orderAddressResult;

}
