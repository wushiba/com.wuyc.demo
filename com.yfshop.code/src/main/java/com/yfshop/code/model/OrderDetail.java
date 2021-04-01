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
 * @since 2021-03-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("yf_order_detail")
public class OrderDetail extends Model<OrderDetail> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * 用户id编号
     */
    private Integer userId;

    /**
     * 订单id
     */
    private Long orderId;

    private Integer merchantId;

    /**
     * pid_path
     */
    private String pidPath;

    /**
     * 收货方式 ZT(自提) | PS(配送)
     */
    private String receiveWay;

    private String isPay;

    /**
     * 商品id
     */
    private Integer itemId;

    /**
     * 商品sku编号
     */
    private Integer skuId;

    /**
     * 商品封面图
     */
    private String itemCover;

    /**
     * 商品sku售价
     */
    private BigDecimal itemPrice;

    /**
     * 购买的商品数量
     */
    private Integer itemCount;

    /** 子订单运费 */
    private BigDecimal freight;

    /** 优惠金额 */
    private BigDecimal couponPrice;

    /** 订单总金额 */
    private BigDecimal orderPrice;

    /** 支付金额 */
    private BigDecimal payPrice;

    /**
     * 优惠金额
     */
    private Long userCouponId;

    /**
     * 订单状态 DZF(待支付), 待发货(DFH), 待收货(DSH), 已完成(YWC)
     */
    private String orderStatus;

    private String itemTitle;

    /**
     * 商品sku的specValueIdpath
     */
    private String specValueIdPath;

    /**
     * 商品sku的规格名称值json串
     */
    private String specNameValueJson;

    /**
     * 商品sku的规格值,"/"拼接(黑色/XXl)
     */
    private String specValueStr;

    /**
     * 订单确认收货时间
     */
    private LocalDateTime confirmTime;

    /**
     * 订单发货时间
     */
    private LocalDateTime shipTime;

    /**
     * 快递公司名称
     */
    private String expressCompany;

    /**
     * 快递单号
     */
    private String expressNo;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
