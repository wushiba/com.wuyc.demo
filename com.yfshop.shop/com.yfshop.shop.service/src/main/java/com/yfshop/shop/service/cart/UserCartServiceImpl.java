package com.yfshop.shop.service.cart;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yfshop.code.mapper.ItemMapper;
import com.yfshop.code.mapper.ItemSkuMapper;
import com.yfshop.code.mapper.UserCartMapper;
import com.yfshop.code.mapper.UserCouponMapper;
import com.yfshop.code.model.Item;
import com.yfshop.code.model.ItemSku;
import com.yfshop.code.model.UserCart;
import com.yfshop.code.model.UserCoupon;
import com.yfshop.common.constants.CacheConstants;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.exception.Asserts;
import com.yfshop.common.util.BeanUtil;
import com.yfshop.shop.dao.UserCartDao;
import com.yfshop.shop.service.cart.result.UserCartResult;
import com.yfshop.shop.service.cart.result.UserCartSummary;
import com.yfshop.shop.service.coupon.request.QueryUserCouponReq;
import com.yfshop.shop.service.coupon.result.YfUserCouponResult;
import com.yfshop.shop.service.coupon.service.FrontUserCouponService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
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
    private UserCartDao customUserCartMapper;
    @Resource
    private ItemSkuMapper skuMapper;
    @Resource
    private ItemMapper itemMapper;
    @Resource
    private FrontUserCouponService frontUserCouponService;
    @Resource
    private UserCouponMapper userCouponMapper;

    @Cacheable(cacheNames = CacheConstants.USER_CART_CACHE_NAME,
            key = "'" + CacheConstants.USER_CART_CACHE_KEY_PREFIX + "' + #root.args[0]")
    @Override
    public List<UserCartResult> queryUserCarts(Integer userId) {
        if (userId == null || userId <= 0) {
            return new ArrayList<>(0);
        }
        // 查询用户所有的购物车列表
        List<UserCart> userCarts = cartMapper.selectList(Wrappers.lambdaQuery(UserCart.class)
                .eq(UserCart::getUserId, userId));
        // 查询sku信息
        List<Integer> skuIdList = userCarts.stream().map(UserCart::getSkuId).collect(Collectors.toList());
        Map<Integer, ItemSku> skuIndexMap = CollectionUtil.isEmpty(skuIdList) ? new HashMap<>(0) :
                skuMapper.selectBatchIds(skuIdList).stream().collect(Collectors.toMap(ItemSku::getId, s -> s));
        List<UserCartResult> userCartResults = BeanUtil.convertList(userCarts, UserCartResult.class);
        for (UserCartResult userCartResult : userCartResults) {
            ItemSku itemSku = skuIndexMap.get(userCartResult.getSkuId());
            if (itemSku == null || "N".equalsIgnoreCase(itemSku.getIsEnable())) {
                userCartResult.setIsAvailable("N");
            } else {
                userCartResult.setSkuSalePrice(itemSku.getSkuSalePrice());
                // 是否有效
                userCartResult.setIsAvailable("Y");
            }
        }
        userCartResults.sort((c1, c2) -> c1.getIsAvailable().equalsIgnoreCase(c2.getIsAvailable()) ? 0 :
                ("Y".equalsIgnoreCase(c1.getIsAvailable()) ? 1 : ("Y".equalsIgnoreCase(c2.getIsAvailable()) ? 1 : -1))
        );
        return userCartResults;
    }

    @CacheEvict(cacheNames = CacheConstants.USER_CART_CACHE_NAME,
            key = "'" + CacheConstants.USER_CART_CACHE_KEY_PREFIX + "' + #root.args[0]")
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
        int rows = customUserCartMapper.addCartNum(userId, skuId, num);
        if (rows <= 0) {
            // 新建购物车项
            UserCart userCart = new UserCart();
            userCart.setSkuTitle(sku.getSkuTitle());
            userCart.setSkuCover(sku.getSkuCover());
            userCart.setSpecValueIdPath(sku.getSpecValueIdPath());
            userCart.setSpecNameValueJson(sku.getSpecNameValueJson());
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

    @CacheEvict(cacheNames = CacheConstants.USER_CART_CACHE_NAME,
            key = "'" + CacheConstants.USER_CART_CACHE_KEY_PREFIX + "' + #root.args[0]")
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
            int rows = customUserCartMapper.updateCartNum(userId, skuId, num);
            if (rows <= 0) {
                // 新建购物车项
                UserCart userCart = new UserCart();
                userCart.setSkuTitle(sku.getSkuTitle());
                userCart.setSkuCover(sku.getSkuCover());
                userCart.setSpecValueIdPath(sku.getSpecValueIdPath());
                userCart.setSpecNameValueJson(sku.getSpecNameValueJson());
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

    @CacheEvict(cacheNames = CacheConstants.USER_CART_CACHE_NAME,
            key = "'" + CacheConstants.USER_CART_CACHE_KEY_PREFIX + "' + #root.args[0]")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void deleteUserCarts(@NotNull(message = "用户ID不能为空") Integer userId,
                                @NotEmpty(message = "购物车列表不能为空") List<Integer> skuIds) throws ApiException {
        cartMapper.delete(Wrappers.lambdaQuery(UserCart.class)
                .eq(UserCart::getUserId, userId).in(UserCart::getSkuId, skuIds));
        return null;
    }

    @CacheEvict(cacheNames = CacheConstants.USER_CART_CACHE_NAME,
            key = "'" + CacheConstants.USER_CART_CACHE_KEY_PREFIX + "' + #root.args[0]")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Void clearUserCart(@NotNull(message = "用户ID不能为空") Integer userId) {
        cartMapper.delete(Wrappers.lambdaQuery(UserCart.class).eq(UserCart::getUserId, userId));
        return null;
    }


    @Override
    public List<UserCartResult> findItemList(Integer skuId, Integer num, String cartIds) {
        List<UserCartResult> resultList = new ArrayList<>();
        if (StringUtils.isNotEmpty(cartIds)) {
            List<Integer> cartIdList = Arrays.stream(StringUtils.split(cartIds, ","))
                    .map(Integer::valueOf)
                    .collect(Collectors.toList());
            List<UserCart> userCartList = cartMapper.selectList(Wrappers.lambdaQuery(UserCart.class)
                    .in(UserCart::getId, cartIdList));
            resultList = BeanUtil.convertList(userCartList, UserCartResult.class);

            List<Integer> skuIdList = userCartList.stream().map(UserCart::getSkuId).collect(Collectors.toList());
            Map<Integer, ItemSku> skuIndexMap = skuMapper.selectBatchIds(skuIdList).stream().collect(Collectors.toMap(ItemSku::getId, s -> s));
            resultList.forEach(data -> {
                ItemSku itemSku = skuIndexMap.get(data.getSkuId());
                if (itemSku != null) {
                    data.setFreight(itemSku.getFreight());
                    data.setSkuSalePrice(itemSku.getSkuSalePrice());
                    data.setCategoryId(itemSku.getCategoryId());
                }
            });
        } else {
            ItemSku itemSku = skuMapper.selectById(skuId);
            UserCartResult userCartResult = BeanUtil.convert(itemSku, UserCartResult.class);
            userCartResult.setNum(num);
            userCartResult.setSkuId(skuId);
            resultList.add(userCartResult);
        }
        return resultList;
    }


    @Override
    public List<UserCartResult> calcUserCart(Integer skuId, Integer num, String cartIds, Long userCouponId) {
        UserCoupon userCoupon = null;
        if (userCouponId != null) {
            userCoupon = userCouponMapper.selectById(userCouponId);
        }
        List<UserCartResult> resultList = new ArrayList<>();
        if (StringUtils.isNotEmpty(cartIds)) {
            List<Integer> cartIdList = Arrays.stream(StringUtils.split(cartIds, ","))
                    .map(Integer::valueOf)
                    .collect(Collectors.toList());
            List<UserCart> userCartList = cartMapper.selectList(Wrappers.lambdaQuery(UserCart.class)
                    .in(UserCart::getId, cartIdList));
            resultList = BeanUtil.convertList(userCartList, UserCartResult.class);

            List<Integer> skuIdList = userCartList.stream().map(UserCart::getSkuId).collect(Collectors.toList());
            Map<Integer, ItemSku> skuIndexMap = skuMapper.selectBatchIds(skuIdList).stream().collect(Collectors.toMap(ItemSku::getId, s -> s));
            resultList.forEach(data -> {
                ItemSku itemSku = skuIndexMap.get(data.getSkuId());
                if (itemSku != null) {
                    data.setFreight(itemSku.getFreight());
                    data.setSkuSalePrice(itemSku.getSkuSalePrice());
                    data.setCategoryId(itemSku.getCategoryId());
                }
            });
        } else {
            ItemSku itemSku = skuMapper.selectById(skuId);
            UserCartResult userCartResult = BeanUtil.convert(itemSku, UserCartResult.class);
            userCartResult.setNum(num);
            userCartResult.setSkuId(skuId);
            resultList.add(userCartResult);
        }

        //计算非火锅的商品价格
        BigDecimal sumPrice = resultList.stream().map(item -> item.getSkuSalePrice().multiply(new BigDecimal(item.getNum()))).reduce(BigDecimal.ZERO, (before, grantMoney) -> NumberUtil.add(before, grantMoney));
        if (userCoupon != null) {
            sumPrice = sumPrice.subtract(new BigDecimal(userCoupon.getCouponPrice()));
        }
        List<UserCartResult> otherGoods = new ArrayList<>();
        resultList.forEach(item -> {
            if (item.getCategoryId() != 3) {
                otherGoods.add(item);
            }
        });
        if (sumPrice.longValue() >= 88) {
            otherGoods.forEach(item -> {
                item.setFreight(BigDecimal.ZERO);
            });
        } else if (sumPrice.longValue() > 0) {
            int sum = otherGoods.stream().mapToInt(UserCartResult::getNum).sum();
            if (userCoupon != null) {
                sum = sum - 1;
            }
            BigDecimal freight = new BigDecimal("10");
            if (sum > 1) {
                freight = freight.divide(new BigDecimal(sum), 2, BigDecimal.ROUND_HALF_UP);
            }
            for (UserCartResult item : otherGoods) {
                if (userCoupon != null && userCoupon.getCanUseItemIds().contains("2032")) {
                    item.setFreight(new BigDecimal("1.8"));
                    if (item.getNum() > 1) {
                        item.setFreight(item.getFreight().add(new BigDecimal(item.getNum() - 1).multiply(freight)));
                    }
                    userCoupon = null;
                } else if (userCoupon != null && userCoupon.getCanUseItemIds().contains("2030")) {
                    item.setFreight(new BigDecimal("18"));
                    if (item.getNum() > 1) {
                        item.setFreight(item.getFreight().add(new BigDecimal(item.getNum() - 1).multiply(freight)));
                    }
                    userCoupon = null;
                } else {
                    item.setFreight(new BigDecimal(item.getNum()).multiply(freight));
                }
            }
        }
        return resultList;
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
            result.setSpecValueIdPath(targetSku.getSpecValueIdPath());
            result.setSpecNameValueJson(targetSku.getSpecNameValueJson());
            result.setSkuSalePrice(targetSku.getSkuSalePrice());
            result.setSkuMarketPrice(targetSku.getSkuMarketPrice());
        }
        return result;
    }

    public static void main(String[] args) {
        BigDecimal freight = new BigDecimal("10");
        int sum = 3;
        System.out.println(freight.divide(new BigDecimal(sum), 2, BigDecimal.ROUND_HALF_UP));
    }

}
