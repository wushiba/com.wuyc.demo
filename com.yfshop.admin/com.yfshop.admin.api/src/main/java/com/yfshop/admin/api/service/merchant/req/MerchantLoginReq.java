package com.yfshop.admin.api.service.merchant.req;

import com.yfshop.common.validate.annotation.Mobile;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class MerchantLoginReq implements Serializable {
    @Mobile(message = "手机号不正确")
    private String mobile;
    @NotNull(message = "密码不能为空")
    private String pwd;
}