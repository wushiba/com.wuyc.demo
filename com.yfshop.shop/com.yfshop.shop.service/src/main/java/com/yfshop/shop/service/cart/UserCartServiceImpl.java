package com.yfshop.shop.service.cart;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yfshop.code.mapper.ItemMapper;
import com.yfshop.code.mapper.ItemSkuMapper;
import com.yfshop.code.mapper.UserCartMapper;
import com.yfshop.code.model.Item;
import com.yfshop.code.model.ItemSku;
import com.yfshop.code.model.UserCart;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.exception.Asserts;
import com.yfshop.common.util.BeanUtil;
import com.yfshop.shop.service.cart.result.UserCartResult;
import com.yfshop.shop.service.cart.result.UserCartSummary;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    @Resource
    private ItemMapper itemMapper;

    @Override
    public List<UserCartResult> queryUserCarts(Integer userId) {
        if (userId == null || userId <= 0) {
            return null;
        }
        // 查询用户所有的购物车列表
        List<UserCart> userCarts = cartMapper.selectList(Wrappers.lambdaQuery(UserCart.class)
                .eq(UserCart::getUserId, userId));
        return BeanUtil.convertList(userCarts, UserCartResult.class);
    }

    @Override
    public UserCartSummary calcUserSelectedCarts(Integer userId, List<Integer> cartIds) {
        if (userId == null || userId <= 0) {
            return null;
        }
        if (CollectionUtil.isEmpty(cartIds)) {
            return UserCartSummary.emptySummary();
        }
        // 查询用户所有的购物车列表
        List<UserCart> userCarts = cartMapper.selectList(Wrappers.lambdaQuery(UserCart.class)
                .eq(UserCart::getUserId, userId).in(UserCart::getId, cartIds));
        return this.calcUserCart(userCarts);
    }

    @Override
    public UserCartSummary calcUserBuySkuDetails(Integer userId, Integer skuId, int num) {
        if (userId == null || userId <= 0 || skuId == null || skuId <= 0 || num <= 0) {
            return null;
        }
        ItemSku sku = skuMapper.selectById(skuId);
        if (sku == null) {
            return UserCartSummary.emptySummary();
        }

        // 总结算价
        BigDecimal totalMoney = NumberUtil.mul(sku.getSkuSalePrice(), num);
        // 购物车总市场价
        BigDecimal oldTotalMoney = NumberUtil.mul(sku.getSkuMarketPrice(), num);
        // 计算运费
        BigDecimal totalFreight = calcTotalFreight();

        // TODO: 2021/3/24 优惠信息

        // 封装数据
        UserCartSummary cartSummary = new UserCartSummary();
        cartSummary.setItemCount(num);
        cartSummary.setTotalMoney(totalMoney);
        cartSummary.setOldTotalMoney(oldTotalMoney);
        cartSummary.setTotalFreight(totalFreight);
        cartSummary.setCarts(null);
        return cartSummary;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void addUserCart(@NotNull(message = "用户ID不能为空") Integer userId,
                            @NotNull(message = "商品SKU不能为空") Integer skuId,
                            @Positive(message = "数量不能为负") int num) throws ApiException {
        ItemSku sku = skuMapper.selectById(skuId);
        Asserts.assertNonNull(sku, 500, "商品SKU不存在");
        Asserts.assertTrue("Y".equalsIgnoreCase(sku.getIsEnable()), 500, "商品SKU已下架");
        Item item = itemMapper.selectById(sku.getItemId());
        Asserts.assertNonNull(item, 500, "商品不存在");
        Asserts.assertTrue("N".equals(item.getIsDelete())
                && "Y".equalsIgnoreCase(item.getIsEnable()), 500, "商品已下架");
        // 修改购物车中商品的数量
        int rows = cartMapper.addCartNum(userId, skuId, num);
        if (rows <= 0) {
            // 新建购物车项
            UserCart userCart = new UserCart();
            userCart.setCreateTime(LocalDateTime.now());
            userCart.setUpdateTime(LocalDateTime.now());
            userCart.setUserId(userId);
            userCart.setItemId(item.getId());
            userCart.setSkuId(skuId);
            userCart.setNum(num);
            cartMapper.insert(userCart);
        }
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void updateUserCart(@NotNull(message = "用户ID不能为空") Integer userId,
                               @NotNull(message = "商品SKU不能为空") Integer skuId,
                               @Min(value = 0L, message = "数量不能为负") int num) throws ApiException {
        ItemSku sku = skuMapper.selectById(skuId);
        Asserts.assertNonNull(sku, 500, "商品SKU不存在");
        Asserts.assertTrue("Y".equalsIgnoreCase(sku.getIsEnable()), 500, "商品SKU已下架");
        Item item = itemMapper.selectById(sku.getItemId());
        Asserts.assertNonNull(item, 500, "商品不存在");
        Asserts.assertTrue("N".equals(item.getIsDelete())
                && "Y".equalsIgnoreCase(item.getIsEnable()), 500, "商品已下架");
        if (num == 0) {
            // 删除该购物项
            cartMapper.delete(Wrappers.lambdaQuery(UserCart.class)
                    .eq(UserCart::getUserId, userId).eq(UserCart::getSkuId, skuId));
        } else {
            // 增加数量
            int rows = cartMapper.updateCartNum(userId, skuId, num);
            if (rows <= 0) {
                // 新建购物车项
                UserCart userCart = new UserCart();
                userCart.setCreateTime(LocalDateTime.now());
                userCart.setUpdateTime(LocalDateTime.now());
                userCart.setUserId(userId);
                userCart.setItemId(item.getId());
                userCart.setSkuId(skuId);
                userCart.setNum(num);
                cartMapper.insert(userCart);
            }
        }
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void deleteUserCarts(@NotNull(message = "用户ID不能为空") Integer userId,
                                @NotEmpty(message = "购物车列表不能为空") List<Integer> skuIds) throws ApiException {
        cartMapper.delete(Wrappers.lambdaQuery(UserCart.class)
                .eq(UserCart::getUserId, userId).in(UserCart::getSkuId, skuIds));
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void clearUserCart(@NotNull(message = "用户ID不能为空") Integer userId) {
        cartMapper.delete(Wrappers.lambdaQuery(UserCart.class).eq(UserCart::getUserId, userId));
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
        BigDecimal totalFreight = calcTotalFreight();

        // TODO: 2021/3/24 优惠信息

        // 封装数据
        UserCartSummary cartSummary = new UserCartSummary();
        cartSummary.setItemCount(itemCount);
        cartSummary.setTotalMoney(totalMoney);
        cartSummary.setOldTotalMoney(oldTotalMoney);
        cartSummary.setTotalFreight(totalFreight);
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

    private BigDecimal calcTotalFreight() {
        throw new UnsupportedOperationException("运费逻辑呢？");
    }
}
