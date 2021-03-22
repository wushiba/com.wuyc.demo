package com.yfshop.admin.api.mall.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author yoush
 * @since 2021-03-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BannerResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * banner名称
     */
    private String bannerName;

    /**
     * home(首页图片) | banner(轮播图)
     */
    private String positions;

    /**
     * 图片地址
     */
    private String imageUrl;

    /**
     * 跳转链接
     */
    private String jumpUrl;

    /**
     * 排序字段
     */
    private Integer sort;

    private String isEnable;
}
