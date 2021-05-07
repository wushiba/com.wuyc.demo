package com.yfshop.admin.api.merchant.result;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class MerchantResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;


    private Integer pid;

    /**
     * 上级名称，有编辑上级名称的时候同步到进来
     */
    private String pMerchantName;

    /**
     * 上级的角色标识
     */
    private String pRoleAlias;

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


    private String openId;

    private String headImgUrl;


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

    private String subAddress;

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

    /**
     * 门头照
     */
    private String headImage;

    private String distance;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private LocalDateTime createTime;
    private String isEnable;
    private String isDelete;
}
