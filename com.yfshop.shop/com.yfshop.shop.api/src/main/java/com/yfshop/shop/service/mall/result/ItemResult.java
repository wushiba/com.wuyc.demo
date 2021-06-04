package com.yfshop.shop.service.mall.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 商品表
 * </p>
 *
 * @author yoush
 * @since 2021-03-22
 */
@Data
public class ItemResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createTime;

    /**
     * 分类id
     */
    private Integer categoryId;

    /**
     * 配送方式 ALL(配送自提都支持), ZT(自提) | PS(配送)
     *
     * @see com.yfshop.common.enums.ReceiveWayEnum
     */
    private String receiveWay;

    /**
     * 商品标题
     */
    private String itemTitle;

    /**
     * 副标题(子标题)
     */
    private String itemSubTitle;

    /**
     * 商品售价
     */
    private BigDecimal itemPrice;

    /**
     * 市场价(划线价)
     */
    private BigDecimal itemMarketPrice;

    private BigDecimal freight;

    /**
     * 库存
     */
    private Integer itemStock;

    /**
     * 商品封面图片
     */
    private String itemCover;

    /**
     * 是否启用该商品(Y|N)
     */
    private String isEnable;

    /**
     * 是否删除， Y(删除)， N（未删除）, 默认未删除
     */
    private String isDelete;

    /**
     * 商品规格数量(颜色,尺寸)
     */
    private Integer specNum;

    /**
     * 排序字段
     */
    private Integer sort;

    /**
     * 分类信息
     */
    private ItemCategoryResult itemCategory;

    /**
     * 商品的详情
     */
    private ItemContentResult itemContent;

    /**
     * 商品图片
     */
    private List<ItemImageResult> itemImages;

    /**
     * 商品的sku信息
     */
    private List<ItemSkuResult> itemSkuList;

    /**
     * 商品的规格
     */
    private List<ItemSpecNameResult> specNames;

    private BigDecimal minSalePrice;
}
