package com.yfshop.admin.api.sourcefactory.result;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 码源工厂信息表
 * </p>
 *
 * @author yoush
 * @since 2021-03-22
 */
@Data
public class SourceFactoryResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * 工厂名称
     */
    private String factoryName;

    /**
     * 工厂联系人
     */
    private String contacts;

    /**
     * 联系电话
     */
    private String mobile;

    /**
     * 邮箱
     */
    private String email;

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

    /**
     * 是否可用: Y(可用), N(禁用)
     */
    private String isEnable;
}
