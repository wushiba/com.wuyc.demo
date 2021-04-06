package com.yfshop.admin.api.merchant.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class MerchantGroupReq implements Serializable {
    private Date startCreateTime;
    private Date endCreateTime;
    private Integer merchantId;
}
