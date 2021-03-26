package com.yfshop.admin.api.coupon.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yfshop.common.validate.annotation.CandidateValue;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@ApiModel
@Data
public class CreateCouponReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    /** 优惠券标题 */
    @NotBlank(message = "优惠券标题不能为空")
    private String couponTitle;

    /** 优惠券发行量 0代表无限量 */
    @NotBlank(message = "优惠券发行量不能为空")
    private Integer couponAmount;

    @NotBlank(message = "每人领取数量不能为空")
    private Integer limitAmount;

    /** 优惠券面值，必须是整数 */
    @NotBlank(message = "优惠券面值不能为空")
    private Integer couponPrice;

    /** 使用条件: 0代表无门槛使用, 其余数字代表到指定数字才可以使用 */
    @NotBlank(message = "使用条件不能为空")
    private BigDecimal useConditionPrice;

    @NotBlank(message = "有效日期类型不能为空")
    @CandidateValue(candidateValue = {"DATE_RANGE", "TODAY", "FIX_DAY"}, message = "有效日期类型标识不正确")
    private String validType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private Date validStartTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private Date validEndTime;

    /** 领取后有效天数 */
    private Integer validDay;

    /** 使用范围类型: ALL(全场通用), (ITEM)指定商品 */
    @NotBlank(message = "领取场景不能为空")
    @CandidateValue(candidateValue = {"ALL", "ITEM"}, message = "使用范围类型标识不正确")
    private String useRangeType;

    /** 指定可使用商品ids */
    private String canUseItemIds;

    @NotBlank(message = "领取场景不能为空")
    @CandidateValue(candidateValue = {"DRAW", "SHOP"}, message = "领取场景标识不正确")
    private String couponResource;

    @NotBlank(message = "优惠券描述不能为空")
    private String couponDesc;
}
