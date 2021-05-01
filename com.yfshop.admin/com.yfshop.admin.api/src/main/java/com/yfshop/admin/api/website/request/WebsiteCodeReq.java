package com.yfshop.admin.api.website.request;

import com.yfshop.common.util.DateUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class WebsiteCodeReq implements Serializable {
    String websiteCode;
    String status;
    Date startTime;
    Date endTime;

    public Date getEndTime() {
        return endTime == null ? null : DateUtil.plusDays(endTime, 1);
    }
}
