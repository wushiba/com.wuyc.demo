package com.yfshop.shop.service.activity.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Title:抽奖活动奖品
 * @Description:
 * @Author:Wuyc
 * @Since:2021-03-24 11:14:43
 * @Version:1.1.0
 * @Copyright:Copyright
 */
@Data
public class YfDrawPrizeResult implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     *
     */
    private Integer id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime updateTime;

    /**
     * 抽奖活动id
     */
    private Integer actId;

    /**
     * 奖品等级, 1 (一等奖), 2(二等奖) 3(三等奖)
     */
    private Integer prizeLevel;

    /**
     * 奖品标题
     */
    private String prizeTitle;

    /**
     * 优惠券id
     */
    private Long couponId;

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

    private Integer smallBoxRate;

    /**
     * 排序
     */
    private Integer sort;

}
