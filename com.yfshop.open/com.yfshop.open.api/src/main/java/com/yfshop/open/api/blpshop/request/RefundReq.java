package com.yfshop.open.api.blpshop.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@NoArgsConstructor
@Data
public class RefundReq implements Serializable {
    private Date beginTime;
    private Date endTime;
    private Integer pageIndex;
    private Integer pageSize;
}
