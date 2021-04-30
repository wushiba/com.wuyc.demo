package com.yfshop.admin.api.merchant.request;

import com.yfshop.common.util.DateUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class MerchantGroupReq implements Serializable {
    private Date startCreateTime;
    private Date endCreateTime;
    private Integer merchantId;


    public Date getEndCreateTime() {
        if (endCreateTime == null) {
            endCreateTime = startCreateTime;
        }
        return endCreateTime == null ? null : DateUtil.plusDays(endCreateTime, 1);
    }
}
