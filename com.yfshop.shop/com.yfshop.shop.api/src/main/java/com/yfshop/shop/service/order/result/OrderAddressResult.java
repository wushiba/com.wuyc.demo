package com.yfshop.shop.service.order.result;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class OrderAddressResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * 订单id
     */
    private Long orderId;

    /**
     * 收货人手机号
     */
    private String mobile;

    /**
     * 收货人姓名
     */
    private String realname;

    /**
     * 省id
     */
    private Integer provinceId;

    /**
     * 省份
     */
    private String province;

    /**
     * 市id
     */
    private Integer cityId;

    /**
     * 市
     */
    private String city;

    /**
     * 区id
     */
    private Integer districtId;

    /**
     * 区
     */
    private String district;

    /**
     * 收货详细地址
     */
    private String address;

}
