package com.yfshop.shop.service.address.result;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户收货地址
 * </p>
 *
 * @author yoush
 * @since 2021-03-22
 */
@Data
public class UserAddressResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * 用户id编号
     */
    private Integer userId;

    /**
     * 是否是默认地址Y(是),N(否)
     */
    private String isDefault;

    /**
     * 姓名
     */
    private String realname;

    /**
     * 手机号码
     */
    private String mobile;

    /**
     * 性别 0:未知 1:男 2:女
     */
    private Integer sex;

    /**
     * 省id
     */
    private Integer provinceId;

    /**
     * 市id
     */
    private Integer cityId;

    /**
     * 区id
     */
    private Integer districtId;

    /**
     * 省份
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

    /**
     * 详细地址信息
     */
    private String address;
}