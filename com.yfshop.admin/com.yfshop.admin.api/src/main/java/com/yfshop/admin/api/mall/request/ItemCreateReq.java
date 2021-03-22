package com.yfshop.admin.api.mall.request;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 创建商品所需要的参数封装类
 *
 * @author Xulg
 * Created in 2019-06-25 13:01
 */
@Data
public class ItemCreateReq implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 商品的发货渠道(后台管理人员需要查看)
     */
    @NotBlank(message = "商品的发货渠道不能为空")
    private String itemDeliveryChannel;

    /**
     * 分类id
     */
    @NotNull(message = "分类id不能为空")
    private Integer categoryId;

    /**
     * 商品标题
     */
    @NotBlank(message = "商品标题不能为空")
    private String title;

    /**
     * 副标题(子标题)
     */
    @NotBlank(message = "商品副标题不能为空")
    private String subTitle;

    /**
     * 商品封面图片
     */
    @NotBlank(message = "商品封面图片不能为空")
    private String itemCover;

    /**
     * 商品售价
     */
    @NotNull(message = "商品售价不能为空")
    @DecimalMin(value = "0", message = "商品售价不能为负")
    private BigDecimal price;

    /**
     * 市场价
     */
    @NotNull(message = "商品市场价不能为空")
    @DecimalMin(value = "0", message = "市场价格不能为负")
    private BigDecimal marketPrice;

    /**
     * 排序字段
     */
    @NotNull(message = "排序字段不能为空")
    @Min(value = 0, message = "排序字段最小值是0")
    private Integer sort;

    /**
     * 商品详情
     */
    @NotBlank(message = "商品详情不能为空")
    private String itemContent;

    /**
     * 商品图片列表
     */
    @NotEmpty(message = "商品图片列表不能为空")
    private List<String> itemImages;
}
