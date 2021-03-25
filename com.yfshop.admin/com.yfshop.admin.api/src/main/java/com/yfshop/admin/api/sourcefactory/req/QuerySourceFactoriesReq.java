package com.yfshop.admin.api.sourcefactory.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Xulg
 * Created in 2021-03-25 19:47
 */
@ApiModel
@Data
public class QuerySourceFactoriesReq implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "页码")
    private Integer pageIndex = 1;
    @ApiModelProperty(value = "每页展示数量")
    private Integer pageSize = 10;
    @ApiModelProperty(value = "工厂名称", required = true)
    private String factoryName;
}
