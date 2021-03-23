package com.yfshop.admin.api.mall.request;

import com.yfshop.common.validate.annotation.CandidateValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
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
@ApiModel
@Data
public class SaveItemSkuReq implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "规格信息", required = true)
    @Valid
    @NotNull(message = "规格信息不能为空")
    private GenerateItemSkuReq specInfo;

    @ApiModelProperty(value = "待创建的SKU列表", required = true)
    @Valid
    @NotNull(message = "待创建的SKU不能为空")
    @Size(min = 1, message = "待创建的SKU列表不能为空")
    private List<ItemCandidateSku> candidateSkus;

    @ApiModel
    @Data
    public static class ItemCandidateSku implements Serializable {
        private static final long serialVersionUID = 1L;

        @ApiModelProperty(value = "商品id", required = true)
        @NotNull(message = "商品id不能为空")
        private Integer itemId;

        @ApiModelProperty(value = "规格名称和值的json串", required = true)
        @NotBlank(message = "规格名称和值的json串不能为空")
        private String specNameValueJson;

        @ApiModelProperty(value = "是否上架", allowableValues = "Y|N", required = true)
        @NotBlank(message = "是否上架不能为空")
        @CandidateValue(candidateValue = {"Y", "N"}, message = "是否上架值必须是Y|N")
        private String isEnable;

        @ApiModelProperty(value = "库存", required = true)
        @NotNull(message = "库存量不能为空")
        @Min(value = 0, message = "库存不能为负")
        private Integer stock;

        @ApiModelProperty(value = "商品售价", required = true)
        @NotNull(message = "商品售价不能为空")
        @DecimalMin(value = "0", message = "商品售价不能为负")
        private BigDecimal price;

        @ApiModelProperty(value = "市场价", required = true)
        @NotNull(message = "市场价不能为空")
        @DecimalMin(value = "0", message = "市场价格不能为负")
        private BigDecimal marketPrice;
    }

}
