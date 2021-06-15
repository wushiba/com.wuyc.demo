package com.yfshop.admin.api.healthy.request;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class QueryHealthySubOrderReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private String pOrderNo;

    private String orderNo;

    private String orderStatus;

    private String postWay;

    private String contracts;

    private String mobile;

    private String address;

    private Integer provinceId;

    private Integer cityId;

    private Integer districtId;

    private Date startTime;

    private Date endTime;

    private String expressCompany;

    private String expressNo;

    private String postKey;

    private Integer pageIndex = 1;

    private Integer pageSize = 10;


}
