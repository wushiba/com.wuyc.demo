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
    private String contacts;
    private String mobile;
    private boolean isHaveWebsite;
    private Integer currentExchange;
    private Integer totalExchange;
    private Integer currentGoodsRecord;
    private Long pages;
    private Long current;
    private Long total;
    private Long size;
    private List<MerchantGroupResult> list = new ArrayList<>();
}
