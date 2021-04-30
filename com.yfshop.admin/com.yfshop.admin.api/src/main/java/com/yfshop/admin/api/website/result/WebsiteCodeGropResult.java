package com.yfshop.admin.api.website.result;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class WebsiteCodeGropResult implements Serializable {
    private int count;
    private Integer currentExchange;
    private Integer totalExchange;
    private Integer currentGoodsRecord;
    private List<WebsiteCodeDataResult> list = new ArrayList<>();

}
