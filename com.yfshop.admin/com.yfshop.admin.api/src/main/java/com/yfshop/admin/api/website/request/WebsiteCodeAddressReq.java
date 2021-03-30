package com.yfshop.admin.api.website.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class WebsiteCodeAddressReq implements Serializable {

    private Integer id;


    /**
     * 商户id
     */
    private Integer merchantId;

    /**
     * 收货地址
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
     * N 不是默认 Y 默认
     */
    private String isDefault;
}
