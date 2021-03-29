package com.yfshop.admin.api.website.req;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.yfshop.common.util.DateUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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
    private Integer pageIndex = 1;
    private Integer pageSize = 10;

    public Date getEndTime() {
        return endTime == null ? null : DateUtil.plusDays(endTime, 1);
    }

}
