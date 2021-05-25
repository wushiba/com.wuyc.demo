package com.yfshop.admin.api.merchant.result;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class MerchantMyselfResult implements Serializable {
    private int count;
    private Integer currentExchange;
    private Integer totalExchange;
    private Integer currentGoodsRecord;
}
