package com.wuyc.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author sp0313
 * @date 2023年07月19日 10:14:00
 */
@Data
public class TestDriveReq implements Serializable {
    private static final long serialVersionUID = 7076610478832620260L;

    /**
     * 姓名
     */
    private String name;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 车系编码
     */
    private String seriesCode;

    /**
     * 车系名称
     */
    private String seriesName;

    /**
     * 车型编码
     */
    private String vehicleCode;

    /**
     * 车系名称
     */
    private String vehicleName;

    /**
     * 经销商ID
     */
    private Long dealerId;

    /**
     * 经销商姓名
     */
    private String dealerName;

    /**
     * 经销商编码
     */
    private String dealerCode;

    /**
     * 省份ID
     */
    private String provinceId;

    /**
     * 省份名称
     */
    private String provinceName;

    /**
     * 城市id
     */
    private String cityId;

    /**
     * 城市名称
     */
    private String cityName;

    /**
     * 用户ID
     */
    private Long accountId;

    /**
     * 邀请码
     */
    private String inviteCode;

    /**
     * yike线索id
     */
    private String yikeClueId;

    /**
     * 经销商区域
     */
    private String areaName;

    /**
     * 经销大区
     */
    private String areaParentName;

}
