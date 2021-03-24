package com.yfshop.admin.api.website.req;

import io.swagger.models.auth.In;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class WebsiteCodeApplyReq implements Serializable {
    Integer pageIndex;
    Integer pageSize;
    String status;
}
