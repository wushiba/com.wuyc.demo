package com.yfshop.admin.api.website.req;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class WebsiteCodeReq implements Serializable {
    String websiteCode;
    String status;
    Date dateTime;
}
