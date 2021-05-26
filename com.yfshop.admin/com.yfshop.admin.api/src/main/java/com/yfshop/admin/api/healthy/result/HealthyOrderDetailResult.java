package com.yfshop.admin.api.healthy.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@ApiModel
@Data
public class HealthyOrderDetailResult implements Serializable {

    private static final long serialVersionUID = 1L;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private LocalDateTime createTime;
    /**
     * 支付时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private LocalDateTime payTime;

    /**
     * 订单取消时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private LocalDateTime cancelTime;
    /**
     * 街道地址
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
     * 订单总金额
     */
    private BigDecimal orderPrice;

    /**
     * 运费
     */
    private BigDecimal freight;

    /**
     * 实际需要支付的金额
     */
    private BigDecimal payPrice;

    private Integer itemSpec;


    private List<HealthySubOrderResult> list;


}
