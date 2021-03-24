package com.yfshop.shop.service.address.request;

import com.yfshop.common.validate.annotation.CandidateValue;
import com.yfshop.common.validate.annotation.Mobile;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author Xulg
 * Created in 2021-03-23 19:30
 */
@Data
public class CreateUserAddressReq implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "收货人姓名", allowableValues = "Y|N", required = true)
    @NotBlank(message = "是否默认地址不能为空")
    @CandidateValue(candidateValue = {"Y", "N"}, message = "是否默认地址的值只能是Y|N")
    private String isDefault;

    @ApiModelProperty(value = "收货人姓名", required = true)
    @NotBlank(message = "收货人姓名不能为空")
    private String realname;

    @ApiModelProperty(value = "手机号", required = true)
    @NotBlank(message = "手机号不能为空")
    @Mobile(message = "手机号格式不正确")
    private String mobile;

    @ApiModelProperty(value = "性别", allowableValues = "0未知|1男|2女", required = true)
    @NotNull(message = "性别不能为空")
    @CandidateValue(candidateValue = {"0", "1", "2"}, message = "错误的性别值")
    private Integer sex = 0;

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
}
