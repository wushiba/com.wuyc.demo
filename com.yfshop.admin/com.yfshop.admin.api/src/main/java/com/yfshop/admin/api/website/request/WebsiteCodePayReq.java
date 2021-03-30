package com.yfshop.admin.api.website.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class WebsiteCodePayReq implements Serializable {
    List<Integer> ids;
    Integer addressId;
}
