package com.yfshop.code.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author yoush
 * @since 2021-04-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("yf_wx_pay_notify")
public class WxPayNotify extends Model<WxPayNotify> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private LocalDateTime createTime;

    private String openId;

    private String bankType;

    /**
     * 订单总金额，单位为分
     */
    private Integer totalFee;

    /**
     * 应结订单金额=订单金额-非充值代金券金额，应结订单金额<=订单金额。
     */
    private Integer settlementTotalFee;

    /**
     * 现金支付金额订单现金支付金额，详见支付金额
     */
    private Integer cashFee;

    /**
     * 微信支付订单号
     */
    private String transactionId;

    /**
     * 商户订单号
     */
    @TableField("outTrade_no")
    private String outtradeNo;

    /**
     * 付完成时间，格式为yyyyMMddHHmmss
     */
    private String timeEnd;

    /**
     * 通知url
     */
    private String notifyUrl;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
