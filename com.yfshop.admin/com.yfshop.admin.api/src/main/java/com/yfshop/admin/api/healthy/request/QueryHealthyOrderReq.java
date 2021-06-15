package com.yfshop.admin.api.healthy.request;

import com.yfshop.common.util.DateUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class QueryHealthyOrderReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private String orderNo;

    private String orderStatus;

    private String contracts;

    private Date startTime;

    private Date endTime;

    private Integer pageIndex = 1;

    private Integer pageSize = 10;

    public Date getEndTime() {
        return endTime == null ? null : DateUtil.plusDays(endTime, 1);
    }

}
