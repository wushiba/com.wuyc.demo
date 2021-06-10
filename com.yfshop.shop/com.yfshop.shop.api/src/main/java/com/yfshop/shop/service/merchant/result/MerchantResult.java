package com.yfshop.shop.service.merchant.result;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.geo.Distance;

import java.io.Serializable;

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

    private Distance distance;

}
