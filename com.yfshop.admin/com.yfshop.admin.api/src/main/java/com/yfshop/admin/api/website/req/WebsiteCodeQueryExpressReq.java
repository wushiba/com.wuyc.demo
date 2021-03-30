package com.yfshop.admin.api.website.req;

import lombok.Data;

import java.io.Serializable;

@Data
public class WebsiteCodeQueryExpressReq implements Serializable {
    private Integer id;
    private String expressName;
    private String expressNo;

}
