package com.yfshop.shop.controller.vo;

import com.yfshop.shop.service.cart.result.UserCartResult;
import com.yfshop.shop.service.coupon.result.YfUserCouponResult;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Xulg
 * Created in 2021-03-24 10:39
 */
@Data
public class UserCartPageData implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<UserCartResult> carts;
    private List<YfUserCouponResult> coupons;
}
