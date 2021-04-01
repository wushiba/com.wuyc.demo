package com.yfshop.shop.service.mall.req;

import lombok.Data;
import org.apache.commons.collections4.map.HashedMap;

import java.io.Serializable;
import java.util.*;

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
