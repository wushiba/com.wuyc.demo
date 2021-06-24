package com.yfshop.code.model;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author yoush
 * @since 2021-06-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("yf_postage_rules")
public class PostageRules extends Model<PostageRules> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String name;

    private BigDecimal conditions;

    private BigDecimal isTrue;

    private BigDecimal isFalse;

    private String skuIds;

    private Integer couponId;

    private BigDecimal exchangeFee;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
