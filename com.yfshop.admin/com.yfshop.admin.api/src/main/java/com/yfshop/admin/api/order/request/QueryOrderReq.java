package com.yfshop.admin.api.order.request;

import com.yfshop.common.util.DateUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class QueryOrderReq implements Serializable {
    private String orderNo;
    private String userName;
    private String receiveWay;
    private String orderStatus;
    private Date startTime;
    private Date endTime;
    private Integer pageIndex = 1;
    private Integer pageSize = 10;

    public Date getEndTime() {
        return endTime == null ? null : DateUtil.plusDays(endTime, 1);
    }
}
