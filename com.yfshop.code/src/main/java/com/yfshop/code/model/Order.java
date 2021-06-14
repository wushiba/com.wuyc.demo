package com.yfshop.code.model;

import java.math.BigDecimal;
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
@TableName("yf_order")
public class Order extends Model<Order> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * 用户id
     */
    private Integer userId;

    private String receiveWay;

    private String websiteCode;

    /**
     * 商品总数量
     */
    private Integer itemCount;

    /**
     * 子订单数量
     */
    private Integer childOrderCount;

    /**
     * 订单总金额
     */
    private BigDecimal orderPrice;

    /**
     * 优惠券面额
     */
    private BigDecimal couponPrice;

    /**
     * 运费
     */
    private BigDecimal freight;

    /**
     * 实际需要支付的金额
     */
    private BigDecimal payPrice;

    private String isPay;

    /**
     * 支付流水编号
     */
    private String billNo;

    private String outOrderNo;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /** 是否取消 */
    private String isCancel;

    /**
     * 订单取消时间
     */
    private LocalDateTime cancelTime;

    /**
     * 支付重试次数
     */
    private Integer payEntryCount;

    /**
     * 订单备注
     */
    private String remark;



    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
