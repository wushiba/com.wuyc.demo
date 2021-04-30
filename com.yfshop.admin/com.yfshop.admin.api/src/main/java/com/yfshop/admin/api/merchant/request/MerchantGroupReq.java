package com.yfshop.admin.api.merchant.request;

import com.yfshop.common.util.DateUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class MerchantGroupReq implements Serializable {
    private Date startTime;
    private Date endTime;
    private Integer merchantId;
    
    public Date getEndTime() {
        if (endTime == null) {
            endTime = getStartTime();
        }
        return endTime == null ? null : DateUtil.plusDays(endTime, 1);
    }
}
