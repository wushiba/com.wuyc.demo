package com.yfshop.shop.service.express.result;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@Data
public class StoExpressResult implements Serializable {
    private String success;
    private String errorCode;
    private String errorMsg;
    private String needRetry;
    private String requestId;
    private String expInfo;
    private String data;


    @NoArgsConstructor
    @Data
    public static class WaybillNoDTO implements Serializable {
        private String waybillNo;
        private String opOrgName;
        private String opOrgCode;
        private String opOrgCityName;
        private String opOrgProvinceName;
        private String opOrgTel;
        private String opTime;
        private String scanType;
        private String opEmpName;
        private String opEmpCode;
        private String memo;
        private String bizEmpName;
        private String bizEmpCode;
        private String bizEmpPhone;
        private String bizEmpTel;
        private String nextOrgName;
        private String nextOrgCode;
        private String issueName;
        private String signoffPeople;
        private Double weight;
        private String containerNo;
        private String orderOrgCode;
        private String orderOrgName;
        private String transportTaskNo;
        private String carNo;
        private String opOrgTypeCode;
    }

}
