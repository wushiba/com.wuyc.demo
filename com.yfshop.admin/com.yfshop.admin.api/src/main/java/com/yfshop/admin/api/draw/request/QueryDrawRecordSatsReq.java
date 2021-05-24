package com.yfshop.admin.api.draw.request;

import com.yfshop.common.util.DateUtil;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@ApiModel
@Data
public class QueryDrawRecordSatsReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private String province;

    private Date startTime;

    private Date endTime;

    public Date getEndTime() {
        if (endTime == null) {
            startTime = DateUtil.plusDays(new Date(), -30);
        }
        return endTime == null ? DateUtil.plusDays(new Date(), 1) : DateUtil.plusDays(endTime, 1);
    }

}
