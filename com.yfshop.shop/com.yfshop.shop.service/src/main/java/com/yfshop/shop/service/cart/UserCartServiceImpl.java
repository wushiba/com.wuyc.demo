package com.yfshop.shop.service.cart;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yfshop.code.mapper.*;
import com.yfshop.code.model.*;
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
import java.util.*;
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
    @Resource
    private PostageRulesMapper postageRulesMapper;

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
    public UserCartSummary calcUserCart(Integer skuId, Integer num, String cartIds, Long userCouponId) {
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
            Map<Integer, Item> itemIndexMap = itemMapper.selectBatchIds(skuIdList).stream().collect(Collectors.toMap(Item::getId, s -> s));
            Map<Integer, ItemSku> skuIndexMap = skuMapper.selectBatchIds(skuIdList).stream().collect(Collectors.toMap(ItemSku::getId, s -> s));
            resultList.forEach(data -> {
                ItemSku itemSku = skuIndexMap.get(data.getSkuId());
                Item item = itemIndexMap.get(data.getItemId());
                if (itemSku != null && item != null) {
                    data.setFreight(itemSku.getFreight());
                    data.setSkuSalePrice(itemSku.getSkuSalePrice());
                    data.setCategoryId(itemSku.getCategoryId());
                    data.setSkuType(itemSku.getSkuType());
                    data.setIsAvailable("Y".equals(item.getIsEnable()) && "N".equals(item.getIsDelete()) ? "Y" : "N");
                }
            });
        } else {
            ItemSku itemSku = skuMapper.selectById(skuId);
            Item item = itemMapper.selectById(itemSku.getItemId());
            UserCartResult userCartResult = BeanUtil.convert(itemSku, UserCartResult.class);
            userCartResult.setNum(num);
            userCartResult.setCategoryId(itemSku.getCategoryId());
            userCartResult.setSkuType(itemSku.getSkuType());
            userCartResult.setSkuId(skuId);
            userCartResult.setIsAvailable("Y".equals(item.getIsEnable()) && "N".equals(item.getIsDelete()) ? "Y" : "N");
            resultList.add(userCartResult);
        }


        return calculationSummary(resultList, userCoupon);
    }


    private UserCartSummary calculationSummary(List<UserCartResult> userCartResult, UserCoupon userCoupon) {
        UserCartSummary userCartSummary = UserCartSummary.emptySummary();
        Map<Integer, PostageRules> postageRulesMap = new HashMap<>();
        Map<Integer, List<UserCartResult>> childItemList = new HashMap<>();
        Set<Integer> tcCategory = new HashSet<>();
        PostageRules couponPostageRule = null;
        if (userCoupon != null) {
            couponPostageRule = postageRulesMapper.selectOne(Wrappers.lambdaQuery(PostageRules.class).eq(PostageRules::getCouponId, userCoupon.getCouponId()));
        }
        for (UserCartResult item : userCartResult) {
            if ("Y".equals(item.getIsAvailable())) {
                //优惠券减扣
                if (userCoupon != null) {
                    userCartSummary.setPayMoney(item.getSkuSalePrice().subtract(new BigDecimal(userCoupon.getCouponPrice())));
                }
                //优惠券减扣下的邮费计算
                if (couponPostageRule != null && couponPostageRule.getSkuIds().contains(item.getSkuId() + "")) {
                    userCartSummary.setExchangeMoney(couponPostageRule.getExchangeFee());
                    userCartSummary.setTotalFreight(couponPostageRule.getIsTrue());
                    userCartSummary.setItemCount(userCartSummary.getItemCount() + 1);
                    item.setNum(item.getNum() - 1);
                    couponPostageRule = null;
                }
                //正常情况的邮费计算
                if (item.getNum() > 0) {
                    userCartSummary.setItemCount(userCartSummary.getItemCount() + item.getNum());
                    userCartSummary.setPayMoney(userCartSummary.getPayMoney().add(item.getSkuSalePrice().multiply(new BigDecimal(item.getNum()))));
                    PostageRules postageRules = postageRulesMapper.selectOne(Wrappers.lambdaQuery(PostageRules.class).apply("FIND_IN_SET('{0}',sku_ids)", item.getSkuId()));
                    if (postageRules != null) {
                        postageRulesMap.put(postageRules.getId(), postageRules);
                        List<UserCartResult> cartResults = childItemList.getOrDefault(postageRules.getId(), new ArrayList<>());
                        cartResults.add(item);
                    }
                }
                //火锅套餐包邮
                if (item.getCategoryId() == 3 && "TC".equals(item.getSkuType())) {
                    tcCategory.add(item.getId());
                }
            }
        }

        //计算邮费按照条件计算情况
        postageRulesMap.forEach((key, value) -> {
            List<UserCartResult> childItem = childItemList.get(key);
            BigDecimal pay = BigDecimal.ZERO;
            int category = childItem.get(0).getCategoryId();
            //排查是火锅套餐
            if (category != 3 && !tcCategory.isEmpty()) {
                for (UserCartResult cartResult : childItem) {
                    pay.add(cartResult.getSkuSalePrice().multiply(new BigDecimal(cartResult.getNum())));
                }
                if (value.getCondition().compareTo(pay) >= 0) {
                    userCartSummary.setTotalFreight(value.getIsTrue());
                } else {
                    userCartSummary.setTotalFreight(value.getIsFalse());
                }
            }
        });
        userCartSummary.setCarts(userCartResult);

        return userCartSummary;
    }
}
