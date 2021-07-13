package com.yfshop.admin.api.mall.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Xulg
 * @since 2021-07-13 10:12
 * Description: TODO 请加入类描述信息
 */
@Data
public class QueryBannerReq implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer pageIndex = 1;
    private Integer pageSize = 10;
    private String positions;
    private String bannerName;
}
