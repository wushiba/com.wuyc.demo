package com.yfshop.admin.api.draw.request;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@ApiModel
@Data
public class SaveProvinceRateReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

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
    private Integer secondPrizeId;

    /** 二等奖小盒中奖概率 */
    private Integer secondSmallBoxWinRate;

    /**
     * 二等奖中奖几率
     */
    private Integer secondWinRate;

    /**
     * 三等奖id
     */
    private Integer thirdPrizeId;

    /**
     * 排序
     */
    private Integer sort;


}
