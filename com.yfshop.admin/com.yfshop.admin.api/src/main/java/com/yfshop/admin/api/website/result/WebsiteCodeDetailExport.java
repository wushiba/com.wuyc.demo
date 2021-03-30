package com.yfshop.admin.api.website.result;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelEntity;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 商户批次码详情导出
 * </p>
 *
 * @author yoush
 * @since 2021-03-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ExcelTarget("网点码详情详细")
public class WebsiteCodeDetailExport implements Serializable {

    private static final long serialVersionUID = 1L;


    @Excel(name = "激活时间", format = "yyyy-MM-dd HH:mm:ss",width=40)
    private LocalDateTime activateTime;

    /**
     * 商户码 3位地区码+6位pid+6位年月日+5位序号
     */
    @Excel(name = "网点码")
    private String alias;

    /**
     * 是否激活, Y|N
     */
    @Excel(name = "结算状态", replace = {"Y_已激活", "N_未激活"})
    private String isActivate;

    /**
     * 绑定商户名称
     */
    @Excel(name = "绑定商户")
    private String merchantName;

    /**
     * 商户手机号码
     */
    @Excel(name = "手机号码")
    private String mobile;


}
