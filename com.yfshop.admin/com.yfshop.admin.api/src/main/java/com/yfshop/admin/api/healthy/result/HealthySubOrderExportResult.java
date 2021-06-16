package com.yfshop.admin.api.healthy.result;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yfshop.common.healthy.enums.HealthySubOrderStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * <p>
 * 抽奖活动奖品表
 * </p>
 *
 * @author yoush
 * @since 2021-05-08
 */
@ExcelTarget("HealthySubOrderExportResult")
@Data
public class HealthySubOrderExportResult implements Serializable {

    private static final long serialVersionUID = 1L;

    @Excel(name = "下单时间", width = 18, format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Excel(name = "主订单号", width = 18)
    private String pOrderNo;

    @Excel(name = "子订单号", width = 18)
    private String orderNo;

    @Excel(name = "商品标题", width = 18)
    private String itemTitle;

    @Excel(name = "商品子标题", width = 18)
    private String itemSubTitle;
    /**
     * 收货人姓名
     */
    @Excel(name = "收货人", width = 18)
    private String contracts;
    /**
     * 收货手机号
     */
    @Excel(name = "手机号", width = 18)
    private String mobile;

    /**
     * 省
     */
    @Excel(name = "收货地区", width = 18)
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
     * 街道地址
     */
    @Excel(name = "详细地址", width = 18)
    private String address;


    @Excel(name = "状态", width = 18)
    private String orderStatus;


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


    public String getCreateTime() {
        return createTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }


    public String getOrderStatus() {
        return HealthySubOrderStatusEnum.getByCode(orderStatus).getDescription();
    }

}
