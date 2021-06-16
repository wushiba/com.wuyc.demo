package com.yfshop.code.model;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 用户优惠券
 * </p>
 *
 * @author yoush
 * @since 2021-03-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("yf_user_coupon")
public class UserCoupon extends Model<UserCoupon> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * 用户id编号
     */
    private Integer userId;

    private Integer merchantId;

    private String pidPath;

    private Long couponId;

    private String couponTitle;

    /**
     * 领取场景: DRAW(抽奖), SHOP(商城)
     */
    private String couponResource;

    /** 用户扫码抽奖的码，yf_act_code_batch_detail表的actCode */
    private String actCode;

    private Integer drawPrizeLevel;

    /** 抽奖活动id */
    private Integer drawActivityId;

    /** 抽奖奖品图标 */
    private String drawPrizeIcon;

    /**
     * 优惠券面值，必须是整数
     */
    private Integer couponPrice;

    /**
     * 使用条件: 0代表无门槛使用, 其余数字代表到指定数字才可以使用
     */
    private BigDecimal useConditionPrice;

    /**
     * 使用范围类型: ALL(全场通用), (ITEM)指定商品
     */
    private String useRangeType;

    /**
     * 指定可使用商品ids
     */
    private String canUseItemIds;

    private LocalDateTime validStartTime;

    private LocalDateTime validEndTime;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户手机号
     */
    private String mobile;

    /**
     * N (未使用)  | Y(已使用)
     */
    private String useStatus;

    /**
     * 优惠券描述
     */
    private String couponDesc;

    private LocalDateTime useTime;

    /**
     * 订单编号
     */
    private Long orderId;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
