package com.yfshop.admin.api.mall.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Xulg
 * Created in 2021-03-22 17:47
 */
@ApiModel
@Data
public class QueryItemReq implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "页码")
    private Integer currentPage = 1;
    @ApiModelProperty(value = "每页展示数量")
    private Integer pageSize = 10;
    @ApiModelProperty(value = "商品ID")
    private Integer itemId;
    @ApiModelProperty(value = "商品名称")
    private String itemTitle;
    @ApiModelProperty(value = "分类ID")
    private Integer categoryId;
    @ApiModelProperty(value = "是否上架", allowableValues = "Y|N")
    private String isEnable;
}
