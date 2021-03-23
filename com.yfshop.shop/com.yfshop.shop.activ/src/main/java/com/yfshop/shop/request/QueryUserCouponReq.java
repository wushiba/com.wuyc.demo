package com.yfshop.shop.request;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import java.io.Serializable;

@ApiModel
@Data
public class QueryUserCouponReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer couponId;

    private String useStatus;

    private Long orderId;

    private Integer pageIndex;

    private Integer pageSize;

    /** 对应 CouponResourceEnum */
    private String couponResource;

}
