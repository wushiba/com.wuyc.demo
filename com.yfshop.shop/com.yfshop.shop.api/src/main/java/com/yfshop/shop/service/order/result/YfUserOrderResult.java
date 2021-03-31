package com.yfshop.shop.service.order.result;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 返回给商城前台用户列表展示的通用返回值
 * @author wuyc
 * created 2021/3/31 15:59
 **/
public class YfUserOrderResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<YfUserOrderItem> itemList;

    private Integer orderId;

    /** 订单状态 */
    private String orderStatus;

    /** 是否支付 Y | N,  */
    private String isPay;

    /** 下单时间 */
    private LocalDateTime createTime;

    /** 快递单号 */
    private String expressNo;

    private

    class YfUserOrderItem implements Serializable {

        private static final long serialVersionUID = 1L;

    }

    class YfUserOrderAddress implements Serializable {

        private static final long serialVersionUID = 1L;

    }

}
