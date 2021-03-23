package com.yfshop.admin.api.mall.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author Xulg
 * Created in 2021-03-22 17:29
 */
@ApiModel(parent = CreateItemCategoryReq.class)
@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateItemCategoryReq extends CreateItemCategoryReq implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "分类ID", required = true)
    @NotNull(message = "分类ID不能为空")
    private Integer categoryId;
}
