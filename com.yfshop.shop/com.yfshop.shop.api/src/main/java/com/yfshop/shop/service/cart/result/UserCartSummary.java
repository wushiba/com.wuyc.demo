package com.yfshop.shop.service.cart.result;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
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

    public static UserCartSummary emptySummary() {
        UserCartSummary userCartSummary = new UserCartSummary();
        userCartSummary.setItemCount(0);
        userCartSummary.setTotalMoney(BigDecimal.ZERO);
        userCartSummary.setOldTotalMoney(BigDecimal.ZERO);
        userCartSummary.setTotalFreight(BigDecimal.ZERO);
        userCartSummary.setCarts(new ArrayList<>(0));
        return userCartSummary;
    }
}
