package com.yfshop.admin.api.push.result;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import com.yfshop.common.enums.UserOrderStatusEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@ExcelTarget("WxPushFailExportResult")
public class WxPushFailExportResult implements Serializable {
    @Excel(name = "用户id")
    private Integer userId;


    @Excel(name = "失败原因", width = 60)
    private String failMsg;


}
