package com.yfshop.code.model;

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
 * 商品规则值表
 * </p>
 *
 * @author yoush
 * @since 2021-03-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("yf_item_spec_value")
public class ItemSpecValue extends Model<ItemSpecValue> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * 商品编号
     */
    private Integer itemId;

    /**
     * 规格名称id
     */
    private Integer specId;

    /**
     * 规格的值
     */
    private String specValue;

    /**
     * 排序字段
     */
    private Integer sort;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
