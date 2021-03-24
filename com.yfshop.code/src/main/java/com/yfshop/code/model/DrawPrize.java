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
 * 抽奖活动奖品表
 * </p>
 *
 * @author yoush
 * @since 2021-03-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("yf_draw_prize")
public class DrawPrize extends Model<DrawPrize> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 抽奖活动id
     */
    private Integer actId;

    /**
     * 奖品等级
     */
    private Integer prizeLevel;

    /**
     * 奖品标题
     */
    private String prizeTitle;

    /**
     * 奖品数量
     */
    private Integer prizeCount;

    /**
     * 奖品图标
     */
    private String prizeIcon;

    /**
     * 中奖几率, 1%存100
     */
    private Integer winRate;

    /**
     * 排序
     */
    private Integer sort;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
