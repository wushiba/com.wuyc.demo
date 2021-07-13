package com.yfshop.admin.api.coupon.request;

import com.yfshop.common.validate.annotation.CandidateValue;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

@ApiModel
@Data
public class QueryUserCouponReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer couponId;

    private String useStatus;

    private Long orderId;

    private String userName;

    private Integer pageIndex;

    private Integer pageSize;

}
