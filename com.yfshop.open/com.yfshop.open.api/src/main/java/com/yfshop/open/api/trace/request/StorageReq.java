package com.yfshop.open.api.trace.request;


import lombok.Data;

import java.io.Serializable;

@Data
public class StorageReq implements Serializable {
    /**
     * 箱码
     */
    private String boxNo;

    /**
     * 经销商编号
     */
    private String dealerNo;

    /**
     * 经销商名称
     */
    private String dealerName;

    /**
     * 经销商地址
     */
    private String dealerAddress;
}
