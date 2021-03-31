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
 * 商品与外部商品关联表
 * </p>
 *
 * @author yoush
 * @since 2021-03-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("yf_rl_item_hotpot")
public class RlItemHotpot extends Model<RlItemHotpot> {

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
     * skuId
     */
    private Integer skuId;

    /**
     * 外部商品标识
     */
    private String outItemNo;

    /**
     * 外部sku标识
     */
    private String outSkuNo;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
