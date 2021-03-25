package com.yfshop.shop.service.coupon.result;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @Title:平台优惠券
 * @Description:
 * @Author:Wuyc
 * @Since:2021-03-23 13:47:17
 * @Version:1.1.0
 * @Copyright:Copyright 
 */
@Data
public class YfCouponResult implements Serializable{

	private static final long serialVersionUID = -1L;
	
    /**  */
    private Integer id;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /** 优惠券标题 */
    private String couponTitle;
	
    /** 优惠券发行量 0代表无限量 */
    private Integer couponAmount;
	
    /** 每人领取数量限制 */
    private Integer limitAmount;
	
    /** 优惠券面值，必须是整数 */
    private Integer couponPrice;
	
    /** 使用条件: 0代表无门槛使用, 其余数字代表到指定数字才可以使用 */
    private Double useConditionPrice;
	
    /** 有效日期类型: DATE_RANGE(日期范围), TODAY(领取当天), FIX_DAY(固定天数) */
    private String validType;
	
    /**  */
    private Date validStartTime;
	
    /**  */
    private Date validEndTime;
	
    /** 领取后有效天数 */
    private Integer validDay;
	
    /** 使用范围类型: 全场通用, 指定商品 */
    private String useRangeType;
	
    /** 指定可使用商品ids */
    private String canUseItemIds;
	
    /** 领取场景: DRAW(抽奖), SHOP(商城) */
    private String couponResource;
	
    /** 优惠券描述 */
    private String couponDesc;
	
    /** 是否上架 Y|N */
    private String isEnable;
	
    /** 是否删除， Y(删除)， N（未删除）, 默认未删除 */
    private String isDelete;

}