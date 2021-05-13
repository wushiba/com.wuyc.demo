package com.yfshop.admin.api.sourcefactory.excel;

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
@ExcelTarget("SourceFactoryExcel")
@Data
public class SourceFactoryExcel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Excel(name = "工厂名称", width = 18)
    @NotBlank(message = "工厂名称不能为空")
    private String factoryName;

    @Excel(name = "工厂联系人", width = 18)
    @NotBlank(message = "工厂联系人不能为空")
    private String contacts;

    @Excel(name = "联系电话", width = 18)
    @NotBlank(message = "联系电话不能为空")
    @Mobile(message = "非法的手机号")
    private String mobile;

    @Excel(name = "邮箱", width = 18)
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "非法的邮箱号")
    private String email;

    @Excel(name = "省", width = 18)
    @NotBlank(message = "省ID不能为空")
    private String province;

    @Excel(name = "市", width = 18)
    @NotBlank(message = "市ID不能为空")
    private String city;

    @Excel(name = "区", width = 18)
    @NotBlank(message = "区ID不能为空")
    private String district;

    @Excel(name = "详细地址", width = 18)
    @NotBlank(message = "详细地址不能为空")
    private String address;

    @NotBlank(message = "是否可用不能为空")
    @CandidateValue(candidateValue = {"Y", "N"}, message = "是否可用值必须是Y|N")
    private String isEnable = "Y";

    private Integer fType = 0;
}
