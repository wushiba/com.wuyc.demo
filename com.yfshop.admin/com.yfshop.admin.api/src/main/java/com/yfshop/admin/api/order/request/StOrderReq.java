package com.yfshop.admin.api.order.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class StOrderReq {
    private String orderNo;
    private String orderSource;
    private String billType;
    private String orderType;
    private SenderDTO sender;
    private ReceiverDTO receiver;
    private CargoDTO cargo;
    private CustomerDTO customer;
    private InternationalAnnexDTO internationalAnnex;
    private String waybillNo;
    private AssignAnnexDTO assignAnnex;
    private String codValue;
    private String freightCollectValue;
    private String timelessType;
    private String productType;
    private List<String> serviceTypeList;
    private ExtendFieldMapDTO extendFieldMap;
    private String remark;
    private String expressDirection;
    private String createChannel;
    private String regionType;
    private InsuredAnnexDTO insuredAnnex;
    private String expectValue;
    private String payModel;

    @NoArgsConstructor
    @Data
    public static class SenderDTO {
        private String name;
        private String tel;
        private String mobile;
        private String postCode;
        private String country;
        private String province;
        private String city;
        private String area;
        private String town;
        private String address;
    }

    @NoArgsConstructor
    @Data
    public static class ReceiverDTO {
        private String name;
        private String tel;
        private String mobile;
        private String postCode;
        private String country;
        private String province;
        private String city;
        private String area;
        private String town;
        private String address;
    }

    @NoArgsConstructor
    @Data
    public static class CargoDTO {
        private String battery;
        private String goodsType;
        private String goodsName;
        private Integer goodsCount;
        private Integer spaceX;
        private Integer spaceY;
        private Integer spaceZ;
        private Integer weight;
        private String goodsAmount;
        private List<CargoItemListDTO> cargoItemList;

        @NoArgsConstructor
        @Data
        public static class CargoItemListDTO {
            private String serialNumber;
            private String referenceNumber;
            private String productId;
            private String name;
            private Integer qty;
            private Integer unitPrice;
            private Integer amount;
            private String currency;
            private Integer weight;
            private String remark;
        }
    }

    @NoArgsConstructor
    @Data
    public static class CustomerDTO {
        private String siteCode;
        private String customerName;
        private String sitePwd;
        private String monthCustomerCode;
    }

    @NoArgsConstructor
    @Data
    public static class InternationalAnnexDTO {
        private String internationalProductType;
        private Boolean customsDeclaration;
        private String senderCountry;
        private String receiverCountry;
    }

    @NoArgsConstructor
    @Data
    public static class AssignAnnexDTO {
        private String takeCompanyCode;
        private String takeUserCode;
    }

    @NoArgsConstructor
    @Data
    public static class ExtendFieldMapDTO {
        private String mapValue;
    }

    @NoArgsConstructor
    @Data
    public static class InsuredAnnexDTO {
        private String insuredValue;
        private String goodsValue;
    }
}
