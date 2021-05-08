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
	
    /** 一等奖中奖几率, 1%存100 */
    private Integer firstWinRate;
	
    /** 一等奖id */
    private Integer secondPrizeId;
	
    /** 二等奖中奖几率 */
    private Integer secondWinRate;
	
    /** 三等奖id */
    private Integer thirdPrizeId;
	
    /** 三等奖中奖几率 */
    private Integer thirdWinRate;
	
    /** 排序 */
    private Integer sort;

	// ------------------------------------------------------------ 附加字段 start ---------------------------------------------------------------------

	/** 分页参数-当前页 */
	private Integer pageIndex;

	/** 分页参数-从第几条开始 */
	private Integer pageStart;

	/** 分页参数-每页条数 */
	private Integer pageSize;

	// ------------------------------------------------------------ 附加字段 end ------------------------------------------------------------------------

}
