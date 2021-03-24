package com.yfshop.shop.service.cart;

import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yfshop.code.mapper.ItemSkuMapper;
import com.yfshop.code.mapper.UserCartMapper;
import com.yfshop.code.model.ItemSku;
import com.yfshop.code.model.UserCart;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.util.BeanUtil;
import com.yfshop.shop.service.cart.result.UserCartResult;
import com.yfshop.shop.service.cart.result.UserCartSummary;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Xulg
 * Created in 2021-03-24 9:57
 */
@DubboService
@Validated
public class UserCartServiceImpl implements UserCartService {

    @Resource
    private UserCartMapper cartMapper;
    @Resource
    private ItemSkuMapper skuMapper;

    @Override
    public List<UserCartResult> queryUserCarts(Integer userId) {
        if (userId == null || userId <= 0) {
            return null;
        }
        // 查询用户所有的购物车列表
        List<UserCart> userCarts = cartMapper.selectList(Wrappers.lambdaQuery(UserCart.class).eq(UserCart::getUserId, userId));
        return BeanUtil.convertList(userCarts, UserCartResult.class);
    }

    @Override
    public UserCartSummary queryUserSelectedCarts(Integer userId, List<Integer> cartIdList) {
        return null;
    }

    @Override
    public UserCartSummary queryUserBuySkuDetails(Integer userId, Integer skuId, Integer num) {
        return null;
    }

    @Override
    public Void addUserCart(Integer userId, Integer skuId, int num) throws ApiException {
        return null;
    }

    @Override
    public Void updateCart(Integer userId, Integer skuId, int num) throws ApiException {
        return null;
    }

    @Override
    public Void removeUserCartItems(Integer userId, List<Integer> skuIds) {
        return null;
    }

    @Override
    public Void clearUserCart(Integer userId) {
        return null;
    }

    private UserCartSummary calcUserCart(List<UserCart> userCarts) {
        // 查询购物车中sku信息
        List<Integer> skuIds = userCarts.stream().map(UserCart::getSkuId)
                .collect(Collectors.toList());
        Map<Integer, ItemSku> skuIndexMap = skuMapper.selectBatchIds(skuIds).stream()
                .collect(Collectors.toMap(ItemSku::getId, sku -> sku));

        // user cart details
        List<UserCartResult> carts = userCarts.stream()
                .map(userCart -> convertUserCart(userCart, skuIndexMap))
                .collect(Collectors.toList());
        // available user cart
        List<UserCartResult> availableUserCarts = carts.stream().filter(c -> "Y".equals(c.getIsAvailable()))
                .collect(Collectors.toList());

        // 购物车总数量
        int itemCount = availableUserCarts.stream().mapToInt(UserCartResult::getNum).sum();
        // 购物车总结算价
        BigDecimal totalMoney = availableUserCarts.stream()
                .map((c) -> NumberUtil.mul(c.getSkuSalePrice(), c.getNum()))
                .reduce(BigDecimal.ZERO, NumberUtil::add);
        // 购物车总市场价
        BigDecimal oldTotalMoney = availableUserCarts.stream()
                .map((c) -> NumberUtil.mul(c.getSkuMarketPrice(), c.getNum()))
                .reduce(BigDecimal.ZERO, NumberUtil::add);
        // 计算运费
        BigDecimal totalFreight = null;

        // 封装数据
        UserCartSummary cartSummary = new UserCartSummary();
        cartSummary.setItemCount(itemCount);
        cartSummary.setTotalMoney(totalMoney);
        cartSummary.setOldTotalMoney(oldTotalMoney);
        cartSummary.setTotalFreight(null);
        cartSummary.setCarts(carts);
        return cartSummary;
    }

    private UserCartResult convertUserCart(UserCart userCart, Map<Integer, ItemSku> skuIndexMap) {
        UserCartResult result = new UserCartResult();
        BeanUtil.copyProperties(userCart, result);
        ItemSku targetSku = skuIndexMap.get(userCart.getSkuId());
        if (targetSku == null) {
            result.setIsAvailable("N");
        } else {
            result.setIsAvailable("Y");
            result.setSkuTitle(targetSku.getSkuTitle());
            result.setSkuSubTitle(targetSku.getSkuSubTitle());
            result.setSkuCover(targetSku.getSkuCover());
            result.setSkuSpecValueIdPath(targetSku.getSpecValueIdPath());
            result.setSkuSpecNameValueJson(targetSku.getSpecNameValueJson());
            result.setSkuSalePrice(targetSku.getSkuSalePrice());
            result.setSkuMarketPrice(targetSku.getSkuMarketPrice());
        }
        return result;
    }
}
