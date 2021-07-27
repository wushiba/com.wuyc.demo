package com.yfshop.admin.api.spread.result;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
public class SpreadWithdrawResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private LocalDateTime settlementTime;

}
