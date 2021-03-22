package com.yfshop.admin.api.mall.request;

import com.yfshop.common.validate.annotation.CandidateValue;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 保存商品sku的请求参数封装类
 *
 * @author Xulg
 * Created in 2019-06-27 19:56
 */
@SuppressWarnings("SpellCheckingInspection")
@Data
public class SaveItemSkuReq implements Serializable {
    private static final long serialVersionUID = 1L;

    @Valid
    @NotNull(message = "规格信息不能为空")
    private GenerateItemSkuReq specInfo;

    @Valid
    @NotNull(message = "待创建的SKU不能为空")
    @Size(min = 1, message = "待创建的SKU列表不能为空")
    private List<ItemCandidateSku> candidateSkus;

    @Data
    public static class ItemCandidateSku implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * 商品id
         */
        @NotNull(message = "商品id不能为空")
        private Integer itemId;

        /**
         * 规格名称和值的json串({"尺码":"38","颜色":"黑色"})
         */
        @NotBlank(message = "规格名称和值的json串不能为空")
        private String specNameValueJson;

        /**
         * 是否可用,上架/下架(Y|N)
         */
        @NotBlank(message = "是否上架不能为空")
        @CandidateValue(candidateValue = {"Y","N"}, message = "是否上架值必须是Y|N")
        private String isEnable;

        /**
         * 库存
         */
        @NotNull(message = "库存量不能为空")
        @Min(value = 0, message = "库存不能为负")
        private Integer stock;

        /**
         * 商品售价(用于下单结算的价格)
         */
        @NotNull(message = "商品售价不能为空")
        @DecimalMin(value = "0", message = "商品售价不能为负")
        private BigDecimal price;

        /**
         * 市场价(用于展示)
         */
        @NotNull(message = "市场价不能为空")
        @DecimalMin(value = "0", message = "市场价格不能为负")
        private BigDecimal marketPrice;
    }

}
