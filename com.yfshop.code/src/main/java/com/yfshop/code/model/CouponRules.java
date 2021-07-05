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
 * 平台优惠券
 * </p>
 *
 * @author yoush
 * @since 2021-07-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("yf_coupon_rules")
public class CouponRules extends Model<CouponRules> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer couponId;

    /**
     * ZY 自营 、QT 其他
     */
    private String couponType;

    private String jumpUrl;

    /**
     * 满足金额触发
     */
    private BigDecimal conditions;

    /**
     * ALL 全场，指定商品
     */
    private String itemIds;


    private String isEnable;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
