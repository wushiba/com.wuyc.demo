package com.yfshop.open.api.blpshop.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@Data
public class RefundReq {
    private Date beginTime;
    private Date endTime;
    private String pageIndex;
    private String pageSize;
}
