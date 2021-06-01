package com.yfshop.admin.api.healthy.request;

import com.yfshop.common.util.DateUtil;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@ApiModel
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

    private Integer pageIndex = 1;

    private Integer pageSize = 10;


}
