package com.yfshop.admin.api.draw.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Title:抽奖活动
 * @Description:
 * @Author:Wuyc
 * @Since:2021-03-24 11:12:29
 * @Version:1.1.0
 * @Copyright:Copyright 
 */
@Data
public class DrawActivityResult implements Serializable{

	private static final long serialVersionUID = -1L;
	
    /**  */
    private Integer id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private LocalDateTime updateTime;

    /** 活动标题 */
    private String actTitle;
	
    /** 活动开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private LocalDateTime startTime;
	
    /** 活动结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private LocalDateTime endTime;
	
    /** 活动banner图地址 */
    private String bannerUrl;
	
    /** 活动描述 */
    private String actDesc;
	
    /**  */
    private String jumpUrl;
	
    /** 是否可用(Y可用, N不可用) */
    private String isEnable;

    private Integer drawCount;
	
	// ------------------------------------------------------------ 附加字段 start ---------------------------------------------------------------------
	
	/** 分页参数-当前页 */
	private Integer pageIndex;
	
	/** 分页参数-从第几条开始 */
	private Integer pageStart;
	
	/** 分页参数-每页条数 */
	private Integer pageSize;
	
	// ------------------------------------------------------------ 附加字段 end ------------------------------------------------------------------------

}
