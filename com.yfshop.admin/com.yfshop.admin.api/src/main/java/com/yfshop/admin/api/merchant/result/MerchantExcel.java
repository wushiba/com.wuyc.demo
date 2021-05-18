package com.yfshop.admin.api.merchant.result;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import lombok.Data;

@ExcelTarget("MerchantExcel")
@Data
public class MerchantExcel {
    @Excel(name = "联系电话", width = 18)
    private String mobile;

    @Excel(name = "详细地址", width = 18)
    private String address;
}