package com.yfshop.admin.api.mall.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 生成sku数据封装类
 *
 * @author Xulg
 * Created in 2019-06-25 13:43
 */
@ApiModel
@Data
public class RecreateItemSkuReq implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "商品编号", required = true)
    @NotNull(message = "商品编号不能为空")
    @Min(value = 1, message = "商品编号最小不能为负")
    private Integer itemId;

    @ApiModelProperty(value = "规格名称和规格值列表")
    private List<ItemSpecNameAndValue> specNameAndValues;
}