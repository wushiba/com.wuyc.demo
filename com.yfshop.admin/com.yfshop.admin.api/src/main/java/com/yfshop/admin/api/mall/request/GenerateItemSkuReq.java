package com.yfshop.admin.api.mall.request;

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
@Data
public class GenerateItemSkuReq implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 商品编号
     */
    @NotNull(message = "商品编号不能为空")
    @Min(value = 1, message = "商品编号最小不能为负")
    private Integer itemId;

    /**
     * 规格名称和规格值列表
     */
    private List<ItemSpecNameAndValue> specNameAndValues;

    @Data
    public static class ItemSpecNameAndValue implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * 规格名称
         */
        private String specName;

        /**
         * 规格值
         */
        private List<String> specValues;

        /**
         * 排序字段
         */
        private Integer sort;
    }
}