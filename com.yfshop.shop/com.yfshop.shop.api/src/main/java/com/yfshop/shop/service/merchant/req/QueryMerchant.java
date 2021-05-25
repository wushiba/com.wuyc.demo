package com.yfshop.shop.service.merchant.req;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class QueryMerchant implements Serializable {
    private String ipStr;
    private Integer provinceId;
    private Integer cityId;
    private Integer districtId;
    private Integer pageIndex = 1;
    private Integer pageSize = 10;

}
