package com.yfshop.admin.api.draw.result;

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
public class YfDrawPrizeResult implements Serializable{

	private static final long serialVersionUID = -1L;
	
    /**  */
    private Integer id;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /** 抽奖活动id */
    private Integer actId;
	
    /** 奖品等级, 1 (一等奖), 2(二等奖) 3(三等奖) */
    private Integer prizeLevel;
	
    /** 奖品标题 */
    private String prizeTitle;
	
    /** 优惠券id */
    private Integer couponId;
	
    /** 奖品数量 */
    private Integer prizeCount;
	
    /** 奖品图标 */
    private String prizeIcon;
	
    /** 中奖几率, 1%存100 */
    private Integer winRate;
	
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
