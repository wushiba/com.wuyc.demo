package com.yfshop.admin.api.merchant;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Xulg
 * Created in 2021-04-01 15:57
 */
@Data
public class MerchantExcel {

    @Excel(name = "创建时间", width = 18, format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @Excel(name = "ID", width = 18)
    private Integer id;
    @Excel(name = "商户名称", width = 18)
    private String merchantName;

    @Excel(name = "省", width = 9)
    private String province;

    @Excel(name = "市", width = 9)
    private String city;

    @Excel(name = "区", width = 9)
    private String district;

    @Excel(name = "角色类型", width = 18)
    private String roleName;

    @Excel(name = "手机号码", width = 18)
    private String mobile;

    @Excel(name = "联系人", width = 18)
    private String contacts;

    @Excel(name = "地址", width = 18)
    private String address;

    @Excel(name = "上级商户", width = 18)
    private String pMerchantName;

    @Excel(name = "是否有光明冰箱", width = 9)
    private String isRefrigerator;

    public String getpMerchantName() {
        return pMerchantName;
    }
}
