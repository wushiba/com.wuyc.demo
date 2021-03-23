package com.yfshop.shop.request;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

@ApiModel
@Data
public class QueryCouponReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private String isEnable;

    private String couponTitle;

    private Integer pageIndex;

    private Integer pageSize;

}
