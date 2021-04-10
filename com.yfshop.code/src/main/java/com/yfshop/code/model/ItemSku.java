package com.yfshop.code.model;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 平台商品sku表
 * </p>
 *
 * @author yoush
 * @since 2021-03-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("yf_item_sku")
public class ItemSku extends Model<ItemSku> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * 商品id
     */
    private Integer itemId;

    /**
     * 一级分类id
     */
    private Integer categoryId;

    /**
     * 商品标题
     */
    private String skuTitle;

    /**
     * 副标题(子标题)
     */
    private String skuSubTitle;

    /**
     * 商品售价(用于下单结算的价格)
     */
    private BigDecimal skuSalePrice;

    /**
     * 市场价
     */
    private BigDecimal skuMarketPrice;

    private BigDecimal freight;

    /**
     * 库存数量
     */
    private Integer skuStock;

    /**
     * 封面图
     */
    private String skuCover;

    /**
     * 规格id字符串(1,2,3,4,5)
     */
    private String specValueIdPath;

    /**
     * 规格名称和值的json串({"尺码":"38","颜色":"黑色"})
     */
    private String specNameValueJson;

    /**
     * 规格id和值id的json串({"123":"131","321":"432"})
     */
    private String specNameIdValueIdJson;

    /**
     * 是否启用该商品(Y|N)
     */
    private String isEnable;

    /**
     * 排序字段
     */
    private Integer sort;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
