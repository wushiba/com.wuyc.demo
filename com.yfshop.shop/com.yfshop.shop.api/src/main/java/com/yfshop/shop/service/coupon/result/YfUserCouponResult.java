package com.yfshop.shop.service.coupon.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @Title:用户优惠券
 * @Description:
 * @Author:Wuyc
 * @Since:2021-03-23 16:24:25
 * @Version:1.1.0
 * @Copyright:Copyright 
 */
@Data
public class YfUserCouponResult implements Serializable{

	private static final long serialVersionUID = -1L;
	
    /**  */
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private LocalDateTime updateTime;

    /** 用户id编号 */
    private Integer userId;
	
    /**  */
    private Integer merchantId;
	
    /**  */
    private String pidPath;
	
    /**  */
    private Integer couponId;
	
    /**  */
    private String couponTitle;
	
    /** 领取场景: DRAW(抽奖), SHOP(商城) */
    private String couponResource;

    /** 抽奖活动id */
    private Integer drawActivityId;

    private Integer drawPrizeLevel;
	
    /** 优惠券面值，必须是整数 */
    private Integer couponPrice;
	
    /** 使用条件: 0代表无门槛使用, 其余数字代表到指定数字才可以使用 */
    private BigDecimal useConditionPrice;
	
    /** 使用范围类型: ALL(全场通用), (ITEM)指定商品 */
    private String useRangeType;
	
    /** 指定可使用商品ids */
    private String canUseItemIds;
	
    /**  */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private LocalDateTime validStartTime;
	
    /**  */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private LocalDateTime validEndTime;
	
    /** 用户昵称 */
    private String nickname;
	
    /** 用户手机号 */
    private String mobile;
	
    /** N (未使用)  | Y(已使用) */
    private String useStatus;
	
    /** 优惠券描述 */
    private String couponDesc;
	
    /**  */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private LocalDateTime useTime;
	
    /** 订单编号 */
    private Integer orderId;
	
}
