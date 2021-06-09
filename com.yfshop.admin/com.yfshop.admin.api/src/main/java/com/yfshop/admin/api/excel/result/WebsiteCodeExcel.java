package com.yfshop.admin.api.excel.result;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import com.yfshop.common.validate.annotation.CandidateValue;
import com.yfshop.common.validate.annotation.Mobile;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author Xulg
 * Created in 2021-03-25 19:22
 */
@Data
public class WebsiteCodeExcel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Excel(name = "省", width = 18)
    private String province;

    @Excel(name = "市", width = 18)
    private String city;

    @Excel(name = "区", width = 18)
    private String district;

    @Excel(name = "详细地址", width = 18)
    private String address;

    @Excel(name = "上级商户", width = 18)
    private String pMerchantName;

    @Excel(name = "商户", width = 18)
    private String merchantName;

    @Excel(name = "手机号", width = 18)
    private String mobile;

    @Excel(name = "联系人", width = 18)
    private String contacts;

    @Excel(name = "子账户数量", width = 18)
    private Integer childCount;

    @Excel(name = "工厂打印数量", width = 18)
    private Integer factoryCount;

    @Excel(name = "邮件申请数量", width = 18)
    private Integer emailCount;

    @Excel(name = "网点码激活数量", width = 18)
    private Integer activeCount;
}
