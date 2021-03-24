package com.yfshop.shop.service.cart.result;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Xulg
 * Created in 2021-03-24 13:44
 */
@Data
public class UserCartSummary implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 购物车中商品件数
     */
    private Integer itemCount;

    /**
     * 购物车商品总金额
     */
    private BigDecimal totalMoney;

    /**
     * 购物车商品原价和
     */
    private BigDecimal oldTotalMoney;

    /**
     * 各平台商品的总运费
     */
    private BigDecimal totalFreight;

    /**
     * 购物车列表
     */
    private List<UserCartResult> carts;
}
