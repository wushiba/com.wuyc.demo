package com.yfshop.admin.api.healthy.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
 * @since 2021-05-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class HealthyItemReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;


    /**
     * 分类id
     */
    private Integer categoryId;

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
     * 排序字段
     */
    private Integer sort;

    /**
     * 商品中瓶数
     */
    private Integer spec;

    private List<String> itemImages;

    /**
     * 配送规则
     * 周期-每次配送数量
     * W表示每周
     * M表示每月
     * W-3,M-4
     */
    private String postRule;

    private Integer pageIndex = 1;

    private Integer pageSize = 10;


}
