package com.yfshop.admin.api.activity.request;

import com.yfshop.common.util.DateUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ActCodeQueryReq implements Serializable {
    private Date startTime;
    private Date endTime;
    private String batchNo;
    private Integer actId;
    private Integer pageIndex = 1;
    private Integer pageSize = 10;

    public Date getEndTime() {
        return endTime == null ? null : DateUtil.plusDays(endTime, 1);
    }

}
