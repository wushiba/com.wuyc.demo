package com.yfshop.shop.service.coupon.request;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

@ApiModel
@Data
public class QueryUserCouponReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer userId;

    /** 优惠券id */
    private Integer couponId;

    /** 奖品等级 */
    private Integer drawPrizeLevel;

    /** 是否可用， 已使用，传Y， 未使用，传N */
    private String isCanUse;

    private Integer itemId;

    private Integer skuId;

    private Long orderId;

}
