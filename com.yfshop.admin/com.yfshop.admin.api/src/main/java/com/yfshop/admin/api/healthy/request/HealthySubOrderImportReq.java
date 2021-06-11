package com.yfshop.admin.api.healthy.request;

import cn.afterturn.easypoi.excel.annotation.Excel;
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
@Data
@EqualsAndHashCode(callSuper = false)
public class HealthySubOrderImportReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @Excel(name = "子订单号", width = 18)
    private String orderNo;

    @Excel(name = "商品标题", width = 18)
    private String itemTitle;

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


}
