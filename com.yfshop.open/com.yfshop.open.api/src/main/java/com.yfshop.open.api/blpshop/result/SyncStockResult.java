package com.yfshop.open.api.blpshop.result;

import lombok.Data;

@Data
public class SyncStockResult {
    private String code;
    private String message;
    private String quantity;
}
