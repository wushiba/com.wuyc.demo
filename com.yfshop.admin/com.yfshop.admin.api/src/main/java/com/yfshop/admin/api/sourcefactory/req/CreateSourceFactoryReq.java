package com.yfshop.admin.api.sourcefactory.req;

import com.yfshop.common.validate.annotation.CandidateValue;
import com.yfshop.common.validate.annotation.Mobile;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author Xulg
 * Created in 2021-03-25 18:54
 */
@ApiModel
@Data
public class CreateSourceFactoryReq implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "工厂名称", required = true)
    @NotBlank(message = "工厂名称不能为空")
    private String factoryName;

    @ApiModelProperty(value = "工厂联系人", required = true)
    @NotBlank(message = "工厂联系人不能为空")
    private String contacts;

    @ApiModelProperty(value = "联系电话", required = true)
    @NotBlank(message = "联系电话不能为空")
    @Mobile(message = "非法的手机号")
    private String mobile;

    @ApiModelProperty(value = "邮箱", required = true)
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "非法的邮箱号")
    private String email;

    @ApiModelProperty(value = "省ID", required = true)
    @NotNull(message = "省ID不能为空")
    private Integer provinceId;

    @ApiModelProperty(value = "市ID", required = true)
    @NotNull(message = "市ID不能为空")
    private Integer cityId;

    @ApiModelProperty(value = "区ID", required = true)
    @NotNull(message = "区ID不能为空")
    private Integer districtId;

    @ApiModelProperty(value = "详细地址", required = true)
    @NotBlank(message = "详细地址不能为空")
    private String address;

    @ApiModelProperty(value = "是否可用", allowableValues = "Y|N", required = true)
    @NotBlank(message = "是否可用不能为空")
    @CandidateValue(candidateValue = {"Y", "N"}, message = "是否可用值必须是Y|N")
    private String isEnable = "Y";

    private Integer fType = 0;
}
