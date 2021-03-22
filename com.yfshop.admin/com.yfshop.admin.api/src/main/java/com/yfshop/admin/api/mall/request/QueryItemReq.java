package com.yfshop.admin.api.mall.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Xulg
 * Created in 2021-03-22 17:47
 */
@Data
public class QueryItemReq implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer pageIndex = 1;
    private Integer pageSize = 10;
    private Integer itemId;
    private String itemName;
    private Integer categoryId;
    private Boolean isEnable;
}
