package com.yfshop.admin.api.mall.request;

import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author xulg
 */
@ApiModel
@Data
public class ItemSpecNameAndValue implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "规格名称")
    private String specName;

    @ApiModelProperty(value = "规格值列表")
    private List<String> specValues;

    @ApiModelProperty(value = "排序字段")
    private Integer sort;

    public static ItemSpecNameAndValue createDefaultSpec() {
        ItemSpecNameAndValue specNameAndValue = new ItemSpecNameAndValue();
        specNameAndValue.setSpecName("默认");
        specNameAndValue.setSpecValues(Lists.newArrayList("默认规格"));
        specNameAndValue.setSort(0);
        return specNameAndValue;
    }
}