package com.yfshop.shop.service.mall.req;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Xulg
 * Created in 2021-03-29 10:42
 */
@Data
public class QueryItemDetailReq implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer itemId;
    private boolean querySku = true;
}
