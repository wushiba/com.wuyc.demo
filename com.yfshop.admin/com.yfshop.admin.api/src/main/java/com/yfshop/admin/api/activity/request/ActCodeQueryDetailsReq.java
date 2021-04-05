package com.yfshop.admin.api.activity.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class ActCodeQueryDetailsReq implements Serializable {
    private Integer batchId;
    /**
     * 活动码
     */
    private String actCode;

    /**
     * 溯源码
     */
    private String traceNo;
    private Integer pageIndex = 1;
    private Integer pageSize = 10;

}
