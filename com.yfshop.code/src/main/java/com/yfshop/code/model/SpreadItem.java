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
 * 
 * </p>
 *
 * @author yoush
 * @since 2021-07-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("yf_spread_item")
public class SpreadItem extends Model<SpreadItem> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String itemName;

    private String itemImageUrl;

    private String jumpUrl;

    private BigDecimal itemPrice;

    /**
     * 一级分佣
     */
    private Integer firstCommission;

    /**
     * 二级分佣
     */
    private Integer secondCommission;

    private String isEnable;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
