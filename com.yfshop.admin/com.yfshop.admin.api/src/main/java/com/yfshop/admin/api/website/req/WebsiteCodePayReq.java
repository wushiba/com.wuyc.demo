package com.yfshop.admin.api.website.req;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class WebsiteCodePayReq implements Serializable {
    List<Integer> ids;
    Integer addressId;
}
