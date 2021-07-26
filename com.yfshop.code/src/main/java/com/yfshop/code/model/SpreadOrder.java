package com.yfshop.code.model;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author yoush
 * @since 2021-07-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("yf_spread_order")
public class SpreadOrder extends Model<SpreadOrder> {

    private static final long serialVersionUID = 1L;

    private Long id;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String orderNo;

    private String itemName;

    private String merchantName;

    private Integer merchantId;

    private String merchantMobile;

    private String merchantRole;

    private String pidName;

    private Integer pid;

    /**
     * 订单金额
     */
    private BigDecimal orderPrice;

    /**
     * 渠道佣金
     */
    private BigDecimal firstCommission;

    /**
     * 上级佣金
     */
    private BigDecimal secondCommission;

    /**
     * WAIT 预结算 SUCCESS 已完成 FAIL 已失败
     */
    private String orderStatus;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
