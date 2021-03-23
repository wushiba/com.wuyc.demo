package com.yfshop.admin.api.website.req;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 商户表
 * </p>
 *
 * @author yoush
 * @since 2021-03-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class WebsiteReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    private Integer pid;

    /**
     * 上级名称，有编辑上级名称的时候同步到进来
     */
    private String pMerchantName;

    /**
     * pid_path
     */
    private String pidPath;

    private String roleAlias;

    private String roleName;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 商户手机号
     */
    private String mobile;

    /**
     * 联系人
     */
    private String contacts;

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 区
     */
    private String district;

    private Integer provinceId;

    private Integer cityId;

    private Integer districtId;

    /**
     * 详细地址
     */
    private String address;

    private String websiteTypeName;

    /**
     * 是否有冰箱， Y(有)， N(无)
     */
    private String isRefrigerator;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 纬度
     */
    private Double latitude;

    private String geoHash;

    private String websiteCode;

    private String openId;

}
