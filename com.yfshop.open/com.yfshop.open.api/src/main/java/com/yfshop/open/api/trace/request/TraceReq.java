package com.yfshop.open.api.trace.request;


import lombok.Data;

import java.io.Serializable;

@Data
public class TraceReq implements Serializable {
    /**
     * 盒码
     */
    private String traceNo;

    /**
     * 箱码
     */
    private String boxNo;

    /**
     * 1001噜渴200ML,1002噜渴458ML
     */
    private String productNo;
}
