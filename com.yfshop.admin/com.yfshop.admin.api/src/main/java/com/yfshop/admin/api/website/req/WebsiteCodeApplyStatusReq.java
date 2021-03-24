package com.yfshop.admin.api.website.req;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class WebsiteCodeApplyStatusReq implements Serializable {
    Integer id;
    Integer pageIndex;
    Integer pageSize;
    String status;
}
