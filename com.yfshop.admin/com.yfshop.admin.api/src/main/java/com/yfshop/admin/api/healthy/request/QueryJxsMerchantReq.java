package com.yfshop.admin.api.healthy.request;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

@Data
public class QueryJxsMerchantReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private String merchantName;

    private String contracts;

    private String mobile;

    private String address;

    private Integer provinceId;

    private Integer cityId;

    private Integer districtId;

    private Integer pageIndex = 1;

    private Integer pageSize = 10;


}
