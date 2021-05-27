package com.yfshop.admin.api.healthy.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@ApiModel
@Data
public class HealthySubOrderResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * 用户id编号
     */
    private Integer userId;

    private String userName;

    /**
     * 订单id
     */
    private Long pOrderId;

    private String pOrderNo;

    private String orderNo;

    /**
     * 指派商户
     */
    private Integer merchantId;

    /**
     * 配送方式：物流|配送
     */
    private String postWay;

    /**
     * 订单状态 待发货(DFH), 待收货(DSH), 已完成(YWC)
     */
    private String orderStatus;

    /**
     * 订单确认收货时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private LocalDateTime confirmTime;

    /**
     * 订单发货时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private LocalDateTime shipTime;

    /**
     * 快递公司名称
     */
    private String expressCompany;

    /**
     * 快递单号
     */
    private String expressNo;

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 区
     */
    private String district;

    private Integer provinceId;

    private Integer cityId;

    private Integer districtId;

    /**
     * 街道地址
     */
    private String address;

    /**
     * 收货手机号
     */
    private String mobile;

    /**
     * 收货人姓名
     */
    private String contracts;


}