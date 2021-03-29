package com.yfshop.admin.api.website.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 商户批次码详情
 * </p>
 *
 * @author yoush
 * @since 2021-03-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class WebsiteCodeResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private LocalDateTime updateTime;

    /**
     * 商户id
     */
    private Integer merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

    private String merchantMobile;

    private String roleName;

    /**
     * 商户pid_path
     */
    private String pidPath;

    /**
     * 批次号 年月日+id编号
     */
    private String batchNo;

    /**
     * 商户码数量
     */
    private Integer quantity;

    /**
     * PENDING待支付, CANCEL已取消， WAIT(待发货), DELIVERY(待收货), SUCCESS(已完成)
     */
    private String orderStatus;

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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private LocalDateTime payTime;


    /**
     * 快递名
     */
    private String expressName;

    /**
     * 订单金额
     */
    private BigDecimal orderAmount;

    /**
     * 邮费
     */
    private BigDecimal postage;

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

    /**
     * 文件地址
     */
    private String fileUrl;

    /**
     * 邮箱地址
     */
    private String email;



}
