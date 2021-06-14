package com.yfshop.admin.api.order.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class WuYouOrderReq {
    private String senderCompany;
    private String recipientDetail;
    private String customerName;
    private String senderRemark;
    private String password;
    private String senderName;
    private String payType;
    private String recipientProvince;
    private String recipientDistrict;
    private String recipientRemark;
    private String codAmount;
    private String recipientName;
    private String senderDetail;
    private String platformOrderId;
    private String goodsNum;
    private String recipientPhone;
    private String freightAmount;
    private String senderDistrict;
    private String weight;
    private String senderMobile;
    private String senderProvince;
    private String userName;
    private String senderCity;
    private String goodsType;
    private String senderPhone;
    private String recipientCompany;
    private String recipientMobile;
    private String recipientCity;
    private Integer orderAndGetBillCode;
    private String waybillCode;
}
