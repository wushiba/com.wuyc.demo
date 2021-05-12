package com.yfshop.admin.api.draw.request;

import com.yfshop.common.util.DateUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.models.auth.In;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@ApiModel
@Data
public class QueryDrawRecordReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private String actTitle;

    private Integer prizeLevel;

    private String prizeTitle;

    private Integer userId;

    private String userName;

    private String location;

    private String useStatus;

    private String actCode;

    private String traceNo;

    private String spec;

    private String dealerName;

    private String dealerAddress;

    private Date startTime;

    private Date endTime;

    private Integer pageIndex = 1;

    private Integer pageSize = 10;

    public Date getEndTime() {
        return endTime == null ? null : DateUtil.plusDays(endTime, 1);
    }

}
