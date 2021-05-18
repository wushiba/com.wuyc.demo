package com.yfshop.admin.api.merchant.request;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import lombok.Data;

import java.io.Serializable;

@ExcelTarget("MerchantExcel")
@Data
public class MerchantExcelReq implements Serializable {
    @Excel(name = "联系电话", width = 18)
    private String mobile;

    @Excel(name = "详细地址", width = 18)
    private String address;
}