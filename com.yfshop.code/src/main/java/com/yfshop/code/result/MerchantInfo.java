package com.yfshop.code.result;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Xulg
 * Created in 2021-03-25 16:30
 */
@Data
public class MerchantInfo implements Serializable {
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

    private String openId;

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
     * 密码
     */
    private String password;

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

    /**
     * 是否可用: Y(可用), N(禁用)
     */
    private String isEnable;

    /**
     * 是否删除， Y(删除)， N（未删除）, 默认未删除
     */
    private String isDelete;

    /**
     * 网点类型id
     */
    private Integer websiteTypeId;

    /**
     * 网点类型名称
     */
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
}
