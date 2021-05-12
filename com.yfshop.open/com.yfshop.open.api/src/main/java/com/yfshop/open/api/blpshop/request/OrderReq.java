package com.yfshop.open.api.blpshop.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class OrderReq implements Serializable {
    /**
     * 订单交易状态(等待买家付款=JH_01，等待卖家发货=JH_02，
     * 等待买家确认收货=JH_03，交易成功=JH_04，
     * 交易关闭=JH_05，所有订单=JH_99)
     * （目前会抓取JH_01,JH_02,JH_05三种状态的订单, 但ERP只会处理JH_02待发货的订单）
     */
    private String orderStatus;
    /**
     * 平台订单号，若不为空，则代表查询单个订单的数据，查询单个订单时，可不传时间、状态等
     */
    private String platOrderNo;
    /**
     * 开始时间(格式:yyyy-MM-dd HH:mm:ss)
     */
    private Date startTime;
    /**
     * 截止时间(格式:yyyy-MM-dd HH:mm:ss)
     */
    private Date endTime;
    /**
     * 订单时间类别(订单修改时间=JH_01，订单创建时间=JH_02) (ERP抓单默认JH_02)
     */
    private String timeType;

    private String shopType;
    /**
     * 页码 1
     */
    private Integer pageIndex = 1;
    /**
     * 每页条数 20
     */
    private Integer pageSize = 20;
}
