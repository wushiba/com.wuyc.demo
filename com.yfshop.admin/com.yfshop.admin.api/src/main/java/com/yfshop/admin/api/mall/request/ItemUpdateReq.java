package com.yfshop.admin.api.mall.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 编辑商品所需要的参数封装类
 *
 * @author Xulg
 * Created in 2019-06-25 13:01
 */
@ApiModel(parent = ItemCreateReq.class)
@EqualsAndHashCode(callSuper = true)
@Data
public class ItemUpdateReq extends ItemCreateReq implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "商品id", required = true)
    @NotNull(message = "商品id不能为空")
    private Integer itemId;
}
