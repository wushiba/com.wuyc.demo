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
 * 商户码批次
 * </p>
 *
 * @author yoush
 * @since 2021-05-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("yf_website_code_group")
public class WebsiteCodeGroup extends Model<WebsiteCodeGroup> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * 商户码数量
     */
    private Integer quantity;

    /**
     * PENDING待支付, CANCEL已取消， WAIT(待发货), DELIVERY(待收货), SUCCESS(已完成)
     */
    private String orderStatus;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 流水单号
     */
    private String billno;

    /**
     * 交易方式: WX(微信)
     */
    private String payMethod;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 快递名
     */
    private String expressName;

    /**
     * 快递编号
     */
    private String expressNo;

    /**
     * 收货地址
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

    private Integer merchantId;

    private String merchantName;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
