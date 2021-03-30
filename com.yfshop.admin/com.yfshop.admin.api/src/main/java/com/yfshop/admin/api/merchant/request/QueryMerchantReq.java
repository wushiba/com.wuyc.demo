package com.yfshop.admin.api.merchant.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Xulg
 * Created in 2021-03-25 14:00
 */
@ApiModel
@Data
public class QueryMerchantReq implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "页码")
    private Integer pageIndex = 1;
    @ApiModelProperty(value = "每页展示数量")
    private Integer pageSize = 10;

    @ApiModelProperty(value = "开始日期")
    private Date startCreateTime;
    @ApiModelProperty(value = "结束日期")
    private Date endCreateTime;
    @ApiModelProperty(value = "商户ID")
    private Integer merchantId;
    @ApiModelProperty(value = "商户名称")
    private String merchantName;
    @ApiModelProperty(value = "省ID", required = true)
    private Integer provinceId;
    @ApiModelProperty(value = "市ID", required = true)
    private Integer cityId;
    @ApiModelProperty(value = "区ID", required = true)
    private Integer districtId;
    @ApiModelProperty(value = "角色类型")
    private String roleAlias;
    @ApiModelProperty(value = "手机号")
    private String mobile;
    @ApiModelProperty(value = "联系人")
    private String contacts;
    @ApiModelProperty(value = "上级商户名称")
    private String pMerchantName;
    @ApiModelProperty(value = "是否启用", allowableValues = "Y|N")
    private String isEnable;
    @ApiModelProperty(value = "是否有光明冰箱", allowableValues = "Y|N")
    private String isRefrigerator;
}
