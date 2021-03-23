package com.yfshop.admin.api.website.result;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 网点记账流水
 * </p>
 *
 * @author yoush
 * @since 2021-03-23
 */
@Data
@EqualsAndHashCode(callSuper = false)

public class WebsiteBillResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

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
    private Integer orderId;

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


}
