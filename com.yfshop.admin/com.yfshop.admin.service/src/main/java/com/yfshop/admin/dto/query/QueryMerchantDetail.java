package com.yfshop.admin.dto.query;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Xulg
 * Created in 2021-03-25 14:00
 */
@Data
public class QueryMerchantDetail implements Serializable {
    private static final long serialVersionUID = 1L;
    private Date startCreateTime;
    private Date endCreateTime;
    private Integer merchantId;
    private String merchantName;
    private Integer provinceId;
    private Integer cityId;
    private Integer districtId;
    private String roleAlias;
    private String mobile;
    private String contacts;
    private String pMerchantName;
    private String isEnable;
    private String isRefrigerator;
}
