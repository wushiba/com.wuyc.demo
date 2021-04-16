package com.yfshop.admin.api.merchant.request;

import com.yfshop.common.enums.GroupRoleEnum;
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
public class MerchantReq implements Serializable {
    private static final long serialVersionUID = 1712420945806506096L;

    private Integer merchantId;

    private Integer pId;

    @ApiModelProperty(value = "商户类型", required = true)
    @NotBlank(message = "商户类型不能为空")
    @CheckEnum(value = GroupRoleEnum.class, whitelist = {"ywy", "fxs", "cxy","wd"}, message = "不支持的商户类型")
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
    @NotBlank(message = "登录密码不能为空")
    private String password;


}
