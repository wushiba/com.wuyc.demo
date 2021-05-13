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
 * @since 2021-05-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("yf_wx_pay_refund")
public class WxPayRefund extends Model<WxPayRefund> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private LocalDateTime createTime;

    private String openId;

    /**
     * 订单总金额，单位为分
     */
    private Integer totalFee;

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
     * 退款订单
     */
    private String refundNo;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
