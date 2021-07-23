package com.yfshop.admin.api.spread.result;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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
public class SpreadOrderResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
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


}
