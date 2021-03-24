package com.yfshop.shop.service.address.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * @author Xulg
 * Created in 2021-03-23 19:44
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateUserAddressReq extends CreateUserAddressReq {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "收货地址ID", required = true)
    @NotNull(message = "地址ID不能为空")
    private Integer userAddressId;
}
