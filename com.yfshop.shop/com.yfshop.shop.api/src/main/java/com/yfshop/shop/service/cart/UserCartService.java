package com.yfshop.shop.service.cart;

import com.yfshop.common.exception.ApiException;
import com.yfshop.shop.service.cart.result.UserCartResult;
import com.yfshop.shop.service.cart.result.UserCartSummary;
import com.yfshop.shop.service.mall.result.ItemResult;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
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
     * 添加商品到购物车
     *
     * @param userId the user id
     * @param skuId  the sku id
     * @param num    添加数量
     * @return void
     * @throws ApiException e
     */
    Void addUserCart(@NotNull(message = "用户ID不能为空") Integer userId,
                     @NotNull(message = "商品SKU不能为空") Integer skuId,
                     @Positive(message = "数量不能为负") int num) throws ApiException;

    /**
     * 更新购物车
     *
     * @param userId the user id
     * @param skuId  the sku id
     * @param num    修改数量
     * @return the result
     * @throws ApiException e
     */
    Void updateUserCart(@NotNull(message = "用户ID不能为空") Integer userId,
                        @NotNull(message = "商品SKU不能为空") Integer skuId,
                        @Min(value = 0L, message = "数量不能为负") int num) throws ApiException;

    /**
     * 批量删除购物车某样商品
     *
     * @param userId the user id
     * @param skuIds the sku id list
     * @return void
     */
    Void deleteUserCarts(@NotNull(message = "用户ID不能为空") Integer userId,
                         @NotEmpty(message = "购物车列表不能为空") List<Integer> skuIds) throws ApiException;

    /**
     * 清空用户购物车
     *
     * @param userId the user id
     * @return void
     */
    Void clearUserCart(@NotNull(message = "用户ID不能为空") Integer userId);

    /**
     * 根据cartId计算购物车支付页面详情
     *
     * @param userId  the user id
     * @param cartIds the cart id list
     * @return the result
     */
    @Deprecated
    UserCartSummary calcUserSelectedCarts(Integer userId, List<Integer> cartIds);

    /**
     * 根据skuId计算购物车支付页面详情
     *
     * @param userId the user id
     * @param skuId  the sku id
     * @param num    the item num
     * @return the result
     */
    @Deprecated
    UserCartSummary calcUserBuySkuDetails(Integer userId, Integer skuId, int num);

    /**
     * 查询订单结算页商品信息
     * @param skuId     skuId
     * @param num       商品数量
     * @param cartIds   购物车ids
     * @return
     */
    List<UserCartResult> findItemList(Integer skuId, Integer num, String cartIds);

}
