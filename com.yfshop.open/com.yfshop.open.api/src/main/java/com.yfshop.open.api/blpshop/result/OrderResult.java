package com.yfshop.open.api.blpshop.result;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class OrderResult {
    private String code;
    private String message;
    private Integer numTotalOrder;
    private List<Order> orders;

    @Data
    public static class Order {
        /**
         * 订单号
         */
        private String platOrderNo;
        /**
         * 订单交易状态(
         * 等待买家付款=JH_01，等待卖家发货=JH_02，
         * 等待买家确认收货=JH_03，交易成功=JH_04，
         * 交易关闭=JH_05，卖家部分发货=JH_08)（
         * 返参中的订单状态需要跟请求参数中的订单状态保持一致，否则会被过滤）
         * （目前会抓取JH_01,JH_02,JH_05三种状态的订单, 但ERP只会处理JH_02待发货的订单)
         */
        private String tradeStatus;
        /**
         * 交易时间(格式:yyyy-MM-dd HH:mm:ss) 起始时间格式不可以为0000-00-00
         */
        private Date tradeTime;
        /**
         * 支付单号（跨境场景必填，申报海关用的支付流水号
         */
        private String payOrderNo;
        /**
         * 州/省
         */
        private String province;
        /**
         * 城市
         */
        private String city;
        /**
         * 区县
         */
        private String area;
        /**
         * 镇/街道
         */
        private String town;
        /**
         * 地址
         */
        private String address;
        /**
         * 邮编
         */
        private String zip;
        /**
         * 电话（电话、手机必填一个）
         */
        private String phone;
        /**
         * 电话（电话、手机必填一个）
         */
        private String mobile;
        /**
         * Email
         */
        private String email;
        /**
         * 邮资
         */
        private BigDecimal postFee;
        /**
         * 货款金额
         */
        private BigDecimal goodsFee;
        /**
         * 合计应收（针对卖家）
         */
        private BigDecimal totalMoney;
        /**
         * 实际支付金额（用户支付金额，已减去优惠金额，开发票给用户时可用此金额
         */
        private BigDecimal realPayMoney;
        /**
         * 订单优惠金额（针对整个订单的优惠)
         */
        private BigDecimal favourableMoney;

        /**
         * 平台优惠金额（由平台承担，优惠金额平台会返给商家，开发票给平台时可使用此金额）
         */
        private BigDecimal platDiscountMoney;

        /**
         * 申通快递、申通快递带纸盒、顺丰普惠、顺丰普惠带纸盒
         */
        private String sendStyle;
        /**
         * 支付时间(格式:yyyy-MM-dd HH:mm:ss)
         */
        private Date payTime;
        /**
         * 收货人姓名
         */
        private String receiverName;
        /**
         * 买家昵称（一般为卖家网名）
         */
        private String nick;
        /**
         * 微信公众号⽀付=JH_WXWeb
         */
        private String payType;
        /**
         * 现金收款(现钞、微信支付宝转账)
         */
        private String shouldPayType;

        private List<GoodInfo> goodInfos;
    }

    @Data
    public static class GoodInfo {
        /**
         * 平台商品ID或SKUID(SKUID优先)（此为平台自动生成的编码或者序号）
         */
        private String productId;
        /**
         * 子订单号（若不填，ERP里用户拆单后会无法发货,对应的是subplatorderno）
         * （拆单逻辑是按照商品来拆分，有几种商品就有几个子订单号。
         * 子订单号可以填写货品编码或者sku的编码只要保证同一主订单号下不重复即可）
         */
        private String subOrderNo;
        /**
         * 货品编码或SKU编码(SKU编码优先)。
         * （一般单规格商品返回货品编码，多规格商品返回能对应到该商品某一子规格的子规格编码。）
         * （用于网店管家对接吉客云可以不传）
         */
        private String tradeGoodsNo;
        /**
         * 平台商品ID（用于吉客云对接）
         */
        private String platGoodsId;
        /**
         * 平台规格ID（用于吉客云对接）
         */
        private String platSkuId;
        /**
         * 外部商家编码（用于吉客云对接）
         */
        private String outItemId;
        /**
         * 外部规格编码（用于吉客云对接）
         */
        private String outSkuId;
        /**
         * 交易商品名称
         */
        private String tradeGoodsName;
        /**
         * 交易商品规格
         */
        private String tradeGoodsSpec;
        /**
         * 商品数量
         */
        private Integer goodsCount;
        /**
         * 单价
         */
        private BigDecimal price;

    }

}
