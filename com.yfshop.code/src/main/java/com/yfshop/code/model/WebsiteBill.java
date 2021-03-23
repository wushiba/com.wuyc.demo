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
 * 网点记账
 * </p>
 *
 * @author yoush
 * @since 2021-03-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("yf_website_bill")
public class WebsiteBill extends Model<WebsiteBill> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * 网点商户id
     */
    private Integer merchantId;

    private String pidPath;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 用户微信昵称
     */
    private String mobile;

    /**
     * 订单id
     */
    private Long orderId;

    /**
     * 商品名
     */
    private String itemTitle;

    /**
     * 支付价格
     */
    private BigDecimal payPrice;

    /**
     * 支付流水编号
     */
    private String billNo;

    /**
     * N 待确认 Y 已确认
     */
    private String isConfirm;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
