package com.yfshop.admin.api.website.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 网点每日记账
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class WebsiteBillDayResult implements Serializable {

    private Double totalAmount;
    private Integer totalQuantity;
    private List<WebsiteBillResult> webSiteBillList;

}
