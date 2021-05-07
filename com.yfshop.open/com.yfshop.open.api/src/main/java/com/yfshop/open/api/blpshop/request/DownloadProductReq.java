package com.yfshop.open.api.blpshop.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class DownloadProductReq implements Serializable {
    /**
     * 平台商品ID（此为平台自动生成的编码或者序号）
     */
    private String platProductId;
    /**
     * 商品名称
     */
    private String productName;
    /**
     * 商品状态(已上架商品=JH_01，已下架商品=JH_02，所有商品=JH_99)
     */
    private String status;

    /**
     * 页码 1
     */
    private Integer pageIndex = 1;
    /**
     * 每页条数 20
     */
    private Integer pageSize = 20;

}
