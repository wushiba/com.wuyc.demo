package com.yfshop.admin.api.push.result;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class WxPushTaskData implements Serializable {
    @Excel(name = "用户id")
    private Integer id;
    @Excel(name = "openId")
    private String openId;

}



