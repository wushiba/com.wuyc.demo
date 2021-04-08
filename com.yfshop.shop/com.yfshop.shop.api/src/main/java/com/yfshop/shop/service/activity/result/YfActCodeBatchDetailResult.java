package com.yfshop.shop.service.activity.result;

import lombok.Data;

import java.io.Serializable;

/**
 * @Title:抽奖活动奖品
 * @Description:
 * @Author:Wuyc
 * @Since:2021-03-24 11:14:43
 * @Version:1.1.0
 * @Copyright:Copyright 
 */
@Data
public class YfActCodeBatchDetailResult implements Serializable{

	private static final long serialVersionUID = -1L;

    /**
     * 活动码 4位活动id+6位年月日+6位随机数+2位crc校验位
     */
    private String actCode;

    /**
     * 加密后的活动码
     */
    private String cipherCode;

    /**
     * 溯源码
     */
    private String traceNo;

    /**
     * 活动id(优惠券活动)
     */
    private Integer actId;

    /**
     * 批次id
     */
    private Integer batchId;

    /** 盒子规格值： big(大盒子) | small(小盒) */
    private String boxSpecVal;

}
