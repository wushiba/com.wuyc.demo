package com.yfshop.admin.api.order.result;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yfshop.common.enums.UserOrderStatusEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Data
@ExcelTarget("OrderExportResult")
public class OrderExportResult implements Serializable {
    @Excel(name = "激活时间", format = "yyyy-MM-dd HH:mm:ss",width=40)
    private Date createTime;

    /**
     * 用户id编号
     */
    @Excel(name = "主订单号", width = 18)
    private Long orderId;

    /**
     * 用户id编号
     */
    @Excel(name = "子订单号", width = 18)
    private String orderNo;

    @Excel(name = "商品", width = 18)
    private String itemTitle;

    @Excel(name = "数量", width = 18)
    private Integer itemCount;

    @Excel(name = "激活时间", format = "yyyy-MM-dd HH:mm:ss",width=40)
    private Date payTime;

    @Excel(name = "支付金额")
    /** 支付金额 */
    private BigDecimal payPrice;

    @Excel(name = "订单状态", width = 18)
    private String orderStatus;

    @Excel(name = "买家", width = 18)
    private String userName;

    /**
     * 收货人姓名
     */
    @Excel(name = "收件人姓名", width = 18)
    private String realname;

    @Excel(name = "收件人手机号", width = 18)
    private String mobile;

    /**
     * 省份
     */
    @Excel(name = "省", width = 18)
    private String province;


    /**
     * 市
     */
    @Excel(name = "市", width = 18)
    private String city;


    /**
     * 区
     */
    @Excel(name = "区", width = 18)
    private String district;

    /**
     * 收货详细地址
     */
    @Excel(name = "详细地址", width = 18)
    private String address;

    /**
     * 快递公司名称
     */
    @Excel(name = "物流公司", width = 18)
    private String expressCompany;

    /**
     * 快递单号
     */
    @Excel(name = "物流单号", width = 18)
    private String expressNo;


    public String getOrderStatus() {
        return UserOrderStatusEnum.getByCode(orderStatus).getDescription();
    }
}
