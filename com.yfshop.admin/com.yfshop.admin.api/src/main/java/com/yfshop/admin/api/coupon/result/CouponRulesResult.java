package com.yfshop.admin.api.coupon.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
public class CouponRulesResult implements Serializable {

    private static final long serialVersionUID = 1L;


    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer couponId;


    /**
     * 满足金额触发
     */
    private BigDecimal conditions;

    /**
     * ALL 全场，指定商品
     */
    private String itemIds;

    private Integer limitCount;


}