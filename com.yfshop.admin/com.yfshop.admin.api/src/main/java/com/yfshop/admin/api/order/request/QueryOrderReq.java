package com.yfshop.admin.api.order.request;

import com.yfshop.common.util.DateUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class QueryOrderReq implements Serializable {
    private Integer orderId;
    private String orderNo;
    private String userName;
    private String receiveWay;
    private String orderStatus;
    private String itemTitle;
    private String isUseCoupon;
    private String expressNo;
    private String actCode;
    private String traceNo;
    private Integer categoryId;
    private String receiverName;
    private String receiverMobile;
    private Double couponName;
    private Date startTime;
    private Date endTime;
    private Integer pageIndex = 1;
    private Integer pageSize = 10;

    public Date getEndTime() {
        return endTime == null ? null : DateUtil.plusDays(endTime, 1);
    }
}
