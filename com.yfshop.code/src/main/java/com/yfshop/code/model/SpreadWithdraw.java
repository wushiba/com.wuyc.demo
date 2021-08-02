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
 *
 * </p>
 *
 * @author yoush
 * @since 2021-07-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("yf_spread_withdraw")
public class SpreadWithdraw extends Model<SpreadWithdraw> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String merchantName;

    private Integer merchantId;

    private String merchantMobile;

    private String merchantRole;

    private BigDecimal withdraw;

    private String billNo;

    private String transactionId;

    /**
     * WAIT 等待 SUCCESS 成功 FAIL 失败
     */
    private String status;

    private LocalDateTime settlementTime;

    private String openId;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
