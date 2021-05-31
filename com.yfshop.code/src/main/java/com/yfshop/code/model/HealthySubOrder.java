package com.yfshop.code.model;

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
 * @since 2021-05-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class HealthySubOrder extends Model<HealthySubOrder> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * 用户id编号
     */
    private Integer userId;

    private String userName;

    /**
     * 订单id
     */
    private Long pOrderId;

    private String pOrderNo;

    private String orderNo;
    /**
     * 指派商户(经销商)
     */
    private Integer merchantId;

    /**
     * 订单分配路径
     */
    private String allocateMerchantPath;

    /**
     * 配送方式：物流|配送
     */
    private String postWay;

    /**
     * 订单状态 待发货(DFH), 待收货(DSH), 已完成(YWC)
     */
    private String orderStatus;

    /**
     * 订单确认收货时间
     */
    private LocalDateTime confirmTime;

    /**
     * 订单发货时间
     */
    private LocalDateTime shipTime;

    /**
     * 预计发货时间
     */
    private LocalDateTime expectShipTime;

    /**
     * 快递公司名称
     */
    private String expressCompany;

    /**
     * 快递单号
     */
    private String expressNo;

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
     * 街道地址
     */
    private String address;

    /**
     * 收货手机号
     */
    private String mobile;

    /**
     * 收货人姓名
     */
    private String contracts;

    /**
     * 配送商品数量
     */
    private Integer postItemCount;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
