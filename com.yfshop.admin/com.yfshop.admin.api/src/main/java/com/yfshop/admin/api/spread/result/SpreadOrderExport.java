package com.yfshop.admin.api.spread.result;

import cn.afterturn.easypoi.excel.annotation.Excel;
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
public class SpreadOrderExport implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    @Excel(name = "创建时间", format = "yyyy-MM-dd HH:mm:ss", width = 40)
    private LocalDateTime createTime;
    @Excel(name = "订单编号", width = 30)
    private String orderNo;
    @Excel(name = "商品名称", width = 30)
    private String itemName;
    @Excel(name = "分销商户", width = 30)
    private String merchantName;
    @Excel(name = "商户手机号码", width = 30)
    private String merchantMobile;
    @Excel(name = "商户类型", width = 30)
    private String merchantRole;
    @Excel(name = "上级", width = 30)
    private String pidName;

    /**
     * 订单金额
     */
    @Excel(name = "订单金额", width = 30)
    private BigDecimal orderPrice;

    /**
     * 渠道佣金
     */
    @Excel(name = "渠道佣金", width = 30)
    private BigDecimal firstCommission;

    /**
     * 上级佣金
     */
    @Excel(name = "上级佣金", width = 30)
    private BigDecimal secondCommission;

    /**
     * WAIT 预结算 SUCCESS 已完成 FAIL 已失败
     */
    @Excel(name = "激活状态", replace = {"预结算_WAIT", "已完成_SUCCESS", "已失败_FAIL"})
    private String orderStatus;


}
