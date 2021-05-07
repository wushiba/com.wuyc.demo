package com.yfshop.open.api.blpshop.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class SyncStockReq implements Serializable {
    /**
     * 平台商品ID
     */
    private String platProductId;
    /**
     * 平台子规格ID
     */
    private String skuId;
    /**
     * 外部商家编码
     */
    private String outerId;
    /**
     * 库存数量
     */
    private Integer quantity;
    /**
     * 外部商家SKU编号
     */
    private String quantityQuantity;


}
