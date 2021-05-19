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
 * @since 2021-05-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("yf_draw_record")
public class DrawRecord extends Model<DrawRecord> {

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
     * 活动标题
     */
    private String actTitle;

    /**
     * 奖品等级, 1 (一等奖), 2(二等奖) 3(三等奖)
     */
    private Integer prizeLevel;

    /**
     * 奖品标题
     */
    private String prizeTitle;

    private Long userCouponId;

    /**
     * 用户id
     */
    private Integer userId;


    /**
     * 用户姓名
     */
    private String userName;

    /**
     * 用户手机号
     */
    private String userMobile;

    /**
     * 用户归属地
     */
    private String userLocation;

    /**
     * 使用状态
     */
    private String useStatus;

    /**
     * 规格
     */
    private String spec;

    /**
     * 活动码
     */
    private String actCode;

    /**
     * 溯源码
     */
    private String traceNo;

    /**
     * 经销商姓名
     */
    private String dealerName;

    /**
     * 经销商地址
     */
    private String dealerAddress;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
