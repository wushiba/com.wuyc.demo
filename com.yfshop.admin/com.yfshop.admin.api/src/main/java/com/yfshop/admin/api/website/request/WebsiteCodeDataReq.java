package com.yfshop.admin.api.website.request;

import com.yfshop.common.util.DateUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class WebsiteCodeDataReq implements Serializable {
    private Date startTime;
    private Date endTime;
    private Integer merchantId;

    public Date getEndTime() {
        if (endTime == null) {
            endTime = startTime;
        }
        return endTime == null ? null : DateUtil.plusDays(endTime, 1);
    }
}
