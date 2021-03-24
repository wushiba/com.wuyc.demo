package com.yfshop.shop.service.cart;

import com.yfshop.common.exception.ApiException;
import com.yfshop.shop.service.cart.result.UserCartResult;
import com.yfshop.shop.service.cart.result.UserCartSummary;

import java.util.List;

/**
 * 用户购物车服务
 *
 * @author Xulg
 * Created in 2021-03-24 9:53
 */
public interface UserCartService {

    /**
     * 获取用户的购物车数据
     *
     * @param userId the user id
     * @return the user cart list
     */
    List<UserCartResult> queryUserCarts(Integer userId);

    /**
     * 根据cartId计算购物车支付页面详情
     *
     * @param userId  the user id
     * @param cartIds the cart id list
     * @return the result
     */
    UserCartSummary calcUserSelectedCarts(Integer userId, List<Integer> cartIds);

    /**
     * 根据skuId计算购物车支付页面详情
     *
     * @param userId the user id
     * @param skuId  the sku id
     * @param num    the item num
     * @return the result
     */
    UserCartSummary calcUserBuySkuDetails(Integer userId, Integer skuId, int num);

    /**
     * 添加商品到购物车
     *
     * @param userId the user id
     * @param skuId  the sku id
     * @param num    添加数量
     * @return void
     * @throws ApiException e
     */
    Void addUserCart(Integer userId, Integer skuId, int num) throws ApiException;

    /**
     * 更新购物车
     *
     * @param userId the user id
     * @param skuId  the sku id
     * @param num    修改数量
     * @return the result
     * @throws ApiException e
     */
    Void updateUserCart(Integer userId, Integer skuId, int num) throws ApiException;

    /**
     * 批量删除购物车某样商品
     *
     * @param userId the user id
     * @param skuIds the sku id list
     * @return void
     */
    Void deleteUserCarts(Integer userId, List<Integer> skuIds);

    /**
     * 清空用户购物车
     *
     * @param userId the user id
     * @return void
     */
    Void clearUserCart(Integer userId);

}
