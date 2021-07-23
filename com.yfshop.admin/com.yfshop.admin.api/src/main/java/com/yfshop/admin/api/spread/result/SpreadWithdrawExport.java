package com.yfshop.admin.api.spread.result;

import cn.afterturn.easypoi.excel.annotation.Excel;
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
public class SpreadWithdrawExport implements Serializable {

    private static final long serialVersionUID = 1L;

    @Excel(name = "创建时间", format = "yyyy-MM-dd HH:mm:ss", width = 40)
    private LocalDateTime createTime;
    @Excel(name = "订单流水", width = 30)
    private String billNo;
    @Excel(name = "微信订单流水", width = 30)
    private String transactionId;

    @Excel(name = "分销商户", width = 30)
    private String merchantName;
    @Excel(name = "商户手机号码", width = 30)
    private String merchantMobile;
    @Excel(name = "商户类型", width = 30)
    private String merchantRole;

    @Excel(name = "提现金额", width = 30)
    private BigDecimal withdraw;

    /**
     * WAIT 等待 SUCCESS 成功 FAIL 失败
     */
    @Excel(name = "结算状态", replace = {"审核中_WAIT", "已完成_SUCCESS", "已失败_FAIL"})
    private String status;

    @Excel(name = "创建时间", format = "yyyy-MM-dd HH:mm:ss", width = 40)
    private LocalDateTime settlementTime;

}
