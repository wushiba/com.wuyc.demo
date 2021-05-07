package com.yfshop.open.api.blpshop.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class SendReq implements Serializable {
    /**
     * 订单发货类别(自己联系物流=JH_01，在线下单=JH_02，无需物流=JH_03，自定义物流=JH_04，家装发货=JH_05，国际物流=JH_06)
     */
    private String sendType;
    /**
     * 快递名称
     */
    private String logisticName;
    /**
     * 快递类别(JH前缀为国内快递 ，JHI为国际快递)详见物流公司代码对照表
     */
    private String logisticType;
    /**
     * 快递运单号（无需物流则返回空）
     */
    private String logisticNo;
    /**
     * 平台订单号
     */
    private String platOrderNo;
    /**
     * 是否拆单发货(拆单=1 ，不拆单=0)
     */
    private String isSplit;
    /**
     * 平台子订单交易单号，支持订单拆分为不同商品不同数量发货,多个商品用"|"隔开,
     * 为空则视为整单发货包含子订单编号和商品发货数量，格式suborderno1:count1|suborderno2:count2发货数量需为大于0的整数（无需物流场景、无拆单发货该字段返回空）
     */
    private String subPlatOrderNo;
    /**
     * 发货人姓名
     */
    private String senderName;
    /**
     * 发货人联系电话
     */
    private String senderTel;
    /**
     * 发货人地址(省市区之间以空格分隔)
     */
    private String senderAddress;
}
