package com.yfshop.admin.api.spread.request;

import com.yfshop.common.util.DateUtil;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class SpreadItemReq implements Serializable {
    private String itemName;
    private String itemImageUrl;
    private String jumpUrl;
    private String isEnable;
    private String key;

    private BigDecimal itemPrice;

    /**
     * 一级分佣
     */
    private Integer firstCommission;

    /**
     * 二级分佣
     */
    private Integer secondCommission;
    private Date startTime;
    private Date endTime;
    private Integer pageIndex = 1;
    private Integer pageSize = 10;


    public Date getEndTime() {
        return endTime == null ? null : DateUtil.plusDays(endTime, 1);
    }
}
