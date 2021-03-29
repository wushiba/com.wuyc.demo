package com.yfshop.admin.api.website.req;

import com.yfshop.common.util.DateUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class WebsiteCodeQueryReq implements Serializable {
    private Date startTime;
    private Date endTime;
    private String batchNo;
    private Integer merchantId;
    private String merchantName;
    private String roleName;
    private String mobile;
    private String orderStatus;
    private String expressNo;

    public Date getEndTime() {
        return endTime==null?null: DateUtil.plusDays(endTime,1);
    }
}
