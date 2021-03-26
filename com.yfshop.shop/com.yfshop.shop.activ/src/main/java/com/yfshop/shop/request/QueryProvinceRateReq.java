package com.yfshop.shop.request;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

@ApiModel
@Data
public class QueryProvinceRateReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer actId;

    private String provinceName;

    private Integer provinceId;

    private Integer firstPrizeId;

    private Integer secondPrizeId;

    private Integer thirdPrizeId;
}
