package com.yfshop.code.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author yoush
 * @since 2021-03-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("yf_order_address")
public class OrderAddress extends Model<OrderAddress> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
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


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
