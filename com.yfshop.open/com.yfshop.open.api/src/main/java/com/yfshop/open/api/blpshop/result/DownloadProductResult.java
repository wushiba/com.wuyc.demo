package com.yfshop.open.api.blpshop.result;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class DownloadProductResult implements Serializable {
    private String code;
    private String message;
    private Integer totalCount;
    private List<Goods> goodsList;

    @Data
    public static class Goods implements Serializable {
        /**
         * 平台商品ID（此为平台自动生成的编码或者序号）
         */
        private String platProductId;
        /**
         * 商品名字
         */
        private String name;
        /**
         * 货品编码（商家编码），能够对应到某一货品。此为编码 一般为商家自己填写。
         */
        private String outerId;
        /**
         * 商品价格
         */
        private BigDecimal price;
        /**
         * 商品数量
         */
        private Integer num;
        /**
         * 商品图片
         */
        private String pictureUrl;
        /**
         * 商品所在仓库编号
         */
        private String whseCode;

        private List<Sku> skus;
    }

    @Data
    public static class Sku implements Serializable {
        private String skuId;
        /**
         * 子规格编码，能够对应到某主商品下的子规格的编码。此为编码 一般为商家自己填写。
         */
        private String skuOuterId;
        /**
         * 规格价格
         */
        private BigDecimal skuPrice;
        /**
         * 规格数量
         */
        private Integer skuQuantity;
        /**
         * 规格名称
         */
        private String skuName;
        /**
         * 规格属性
         */
        private String skuProperty;
        /**
         * 规格图片URL
         */
        private String skuPictureUrl;

    }

}
