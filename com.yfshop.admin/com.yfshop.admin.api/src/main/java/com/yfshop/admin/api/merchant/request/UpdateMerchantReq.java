package com.yfshop.admin.api.merchant.request;

import com.yfshop.common.enums.GroupRoleEnum;
import com.yfshop.common.validate.annotation.CandidateValue;
import com.yfshop.common.validate.annotation.CheckEnum;
import com.yfshop.common.validate.annotation.Mobile;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author Xulg
 * Created in 2021-03-25 11:21
 */
@ApiModel
@Data
public class UpdateMerchantReq implements Serializable {
    private static final long serialVersionUID = 1712420945806506096L;

    @ApiModelProperty(value = "商户ID", required = true)
    @NotNull(message = "商户ID不能为空")
    private Integer merchantId;

    @ApiModelProperty(value = "商户类型", required = true)
    @NotBlank(message = "商户类型不能为空")
    @CheckEnum(value = GroupRoleEnum.class, whitelist = {"zb", "fgs", "jxs", "ywy", "cxy", "wd"}, message = "不支持的商户类型")
    private String roleAlias;

    @ApiModelProperty(value = "商户名称", required = true)
    @NotBlank(message = "商户名称不能为空")
    private String merchantName;

    @ApiModelProperty(value = "联系人", required = true)
    @NotBlank(message = "联系人不能为空")
    private String contacts;

    @ApiModelProperty(value = "登录手机号", required = true)
    @NotBlank(message = "登录手机号不能为空")
    @Mobile(message = "非法的手机号")
    private String mobile;

    @ApiModelProperty(value = "登录密码", required = true)
    //@NotBlank(message = "登录密码不能为空")
    private String password;

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

    @ApiModelProperty(value = "上级商户ID", required = false)
    private Integer pid;

    @ApiModelProperty(value = "是否有光明冰箱", allowableValues = "Y|N", required = false)
    //@NotBlank(message = "是否有光明冰箱不能为空")
    //@CandidateValue(candidateValue = {"Y", "N"}, message = "是否有光明冰箱只能是Y|N")
    private String isRefrigerator;

    @ApiModelProperty(value = "门头照", required = false)
    private String headImage;
}
