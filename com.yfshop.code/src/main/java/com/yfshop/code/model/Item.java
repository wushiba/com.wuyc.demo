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
 * 商品表
 * </p>
 *
 * @author yoush
 * @since 2021-03-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("yf_item")
public class Item extends Model<Item> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * 分类id
     */
    private Integer categoryId;

    /**
     * 配送方式 ALL(配送自提都支持), ZT(自提) | PS(配送)
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


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
