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
 * 抽奖-省份定制中奖几率
 * </p>
 *
 * @author yoush
 * @since 2021-03-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("yf_draw_province_rate")
public class DrawProvinceRate extends Model<DrawProvinceRate> {

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
     * 省id
     */
    private Integer provinceId;

    /**
     * 省名称
     */
    private String provinceName;

    /**
     * 一等奖id
     */
    private Integer firstPrizeId;

    /**
     * 一等奖中奖几率, 1%存100
     */
    private Integer firstWinRate;

    /**
     * 一等奖id
     */
    private Integer sencondPrizeId;

    /**
     * 二等奖中奖几率
     */
    private Integer sencondWinRate;

    /**
     * 三等奖id
     */
    private Integer thirdPrizeId;

    /**
     * 三等奖中奖几率
     */
    private Integer thirdWinRate;

    /**
     * 排序
     */
    private Integer sort;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
