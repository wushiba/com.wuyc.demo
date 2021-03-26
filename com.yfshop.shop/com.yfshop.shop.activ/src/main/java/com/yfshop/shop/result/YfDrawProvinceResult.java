package com.yfshop.shop.result;

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
public class YfDrawProvinceResult implements Serializable{

	private static final long serialVersionUID = -1L;
	
    /**  */
    private Integer id;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /** 抽奖活动id */
    private Integer actId;
	
    /** 省id */
    private Integer provinceId;
	
    /** 省名称 */
    private String provinceName;
	
    /** 一等奖id */
    private Integer firstPrizeId;
	
    /** 一等奖id */
    private Integer secondPrizeId;
	
    /** 三等奖id */
    private Integer thirdPrizeId;
	
    /** 排序 */
    private Integer sort;

}
