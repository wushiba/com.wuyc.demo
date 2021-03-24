package com.yfshop.shop.service.coupon.request;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

@ApiModel
@Data
public class QueryUserCouponReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer couponId;

    /** 是否使用， 已使用，传Y， 未使用，传N */
    private String useStatus;

    private Long orderId;

    /** 可用, 传N， 不可用传Y */
    private String isCanUse;

}
