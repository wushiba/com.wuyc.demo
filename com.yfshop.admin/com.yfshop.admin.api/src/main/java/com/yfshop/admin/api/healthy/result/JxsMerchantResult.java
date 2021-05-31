package com.yfshop.admin.api.healthy.result;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

@ApiModel
@Data
public class JxsMerchantResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private String merchantName;

    private String contracts;

    private String mobile;

    private String address;

    private Integer provinceId;

    private String province;

    private Integer cityId;

    private String city;

    private Integer districtId;

    private String district;
}
