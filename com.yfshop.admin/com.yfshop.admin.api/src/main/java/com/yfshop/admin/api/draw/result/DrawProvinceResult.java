package com.yfshop.admin.api.draw.result;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Title:抽奖省份定制化中奖几率
 * @Description:
 * @Author:Wuyc
 * @Since:2021-03-24 11:13:23
 * @Version:1.1.0
 * @Copyright:Copyright 
 */
@Data
public class DrawProvinceResult implements Serializable{

	private static final long serialVersionUID = -1L;
	
    /**  */
    private Integer id;

    /** 抽奖活动id */
    private Integer actId;
	
    /** 省id */
    private Integer provinceId;
	
    /** 省名称 */
    private String provinceName;
	
    /** 一等奖id */
    private Integer firstPrizeId;
	
    /** 一等奖中奖几率, 1%存100 */
    private Integer firstWinRate;
	
    /** 一等奖id */
    private Integer secondPrizeId;
	
    /** 二等奖中奖几率 */
    private Integer secondWinRate;

    /** 二等奖小盒中奖概率 */
    private Integer secondSmallBoxWinRate;
	
    /** 三等奖id */
    private Integer thirdPrizeId;
	
    /** 三等奖中奖几率 */
    private Integer thirdWinRate;
	
    /** 排序 */
    private Integer sort;

}
