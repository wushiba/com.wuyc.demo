package com.yfshop.code.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 雨帆抽奖活动
 * </p>
 *
 * @author yoush
 * @since 2021-03-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("yf_draw_activity")
public class DrawActivity extends Model<DrawActivity> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * 活动标题
     */
    private String actTitle;

    /**
     * 活动开始时间
     */
    private LocalDateTime startTime;

    /**
     * 活动结束时间
     */
    private LocalDateTime endTime;

    /**
     * 活动banner图地址
     */
    private String bannerUrl;

    /**
     * 活动描述
     */
    private String actDesc;

    private String jumpUrl;

    /**
     * 是否可用(Y可用, N不可用)
     */
    private String isEnable;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
