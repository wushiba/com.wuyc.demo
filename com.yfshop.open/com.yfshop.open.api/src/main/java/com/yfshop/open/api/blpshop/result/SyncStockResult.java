package com.yfshop.open.api.blpshop.result;

import lombok.Data;

import java.io.Serializable;

@Data
public class SyncStockResult implements Serializable {
    private String code;
    private String message;
    private String quantity;
}
