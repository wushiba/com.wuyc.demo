package com.yfshop.open.api.blpshop.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class CheckRefundStatusReq implements Serializable {
    private String orderId;
}
