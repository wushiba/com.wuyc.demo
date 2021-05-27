package com.yfshop.code.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 商品图片表
 * </p>
 *
 * @author yoush
 * @since 2021-05-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class HealthyAct extends Model<HealthyAct> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * 商品编号
     */
    private String actName;

    /**
     * 商品编号
     */
    private Integer itemId;

    /**
     * 图片地址
     */
    private String imageUrl;

    /**
     * 排序字段
     */
    private Integer sort;

    private String isEnable;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
