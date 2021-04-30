package com.yfshop.admin.api.merchant.result;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class MerchantGroupResult implements Serializable {
    private int count;
    private Integer merchantId;
    private String merchantName;
    private boolean isHaveWebsite;
    private Integer currentExchange;
    private Integer totalExchange;
    private Integer currentGoodsRecord;
    private List<MerchantGroupResult> list = new ArrayList<>();

}
