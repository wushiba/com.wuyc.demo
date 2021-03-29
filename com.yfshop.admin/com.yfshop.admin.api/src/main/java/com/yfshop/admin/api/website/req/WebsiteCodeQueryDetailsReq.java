package com.yfshop.admin.api.website.req;

import lombok.Data;

import java.io.Serializable;

@Data
public class WebsiteCodeQueryDetailsReq implements Serializable {
    private Integer merchantId;
    private String merchantName;
    private String mobile;
    private String batchNo;
    private String alias;
    private String isActivate;
    private Integer pageIndex = 1;
    private Integer pageSize = 10;

}
