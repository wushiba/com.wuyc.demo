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
 * @since 2021-03-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("yf_website_code")
public class WebsiteCode extends Model<WebsiteCode> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * 商户id
     */
    private Integer merchartId;

    private Integer roleId;

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
     * 订单状态: 待支付, 已取消， WAIT(待发货), DELIVERY(待收货), SUCCESS(已完成)
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
     * 快递名
     */
    private String expreeName;

    /**
     * 订单金额
     */
    private String orderAmount;

    /**
     * 邮费
     */
    private String postage;

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


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
