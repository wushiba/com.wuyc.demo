package com.yfshop.shop.service.order;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.request.BaseWxPayRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.yfshop.code.mapper.*;
import com.yfshop.code.model.*;
import com.yfshop.common.constants.CacheConstants;
import com.yfshop.common.enums.PayPrefixEnum;
import com.yfshop.common.enums.ReceiveWayEnum;
import com.yfshop.common.enums.UserCouponStatusEnum;
import com.yfshop.common.enums.UserOrderStatusEnum;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.exception.Asserts;
import com.yfshop.common.service.RedisService;
import com.yfshop.common.util.BeanUtil;
import com.yfshop.shop.dao.OrderDao;
import com.yfshop.shop.service.activity.result.YfDrawActivityResult;
import com.yfshop.shop.service.activity.result.YfDrawPrizeResult;
import com.yfshop.shop.service.activity.service.FrontDrawRecordService;
import com.yfshop.shop.service.activity.service.FrontDrawService;
import com.yfshop.shop.service.address.UserAddressService;
import com.yfshop.shop.service.address.result.UserAddressResult;
import com.yfshop.shop.service.cart.UserCartService;
import com.yfshop.shop.service.cart.result.UserCartResult;
import com.yfshop.shop.service.cart.result.UserCartSummary;
import com.yfshop.shop.service.coupon.FrontUserCouponServiceImpl;
import com.yfshop.shop.service.coupon.result.YfUserCouponResult;
import com.yfshop.shop.service.coupon.service.FrontUserCouponService;
import com.yfshop.shop.service.mall.MallService;
import com.yfshop.shop.service.mall.req.QueryItemDetailReq;
import com.yfshop.shop.service.mall.result.ItemResult;
import com.yfshop.shop.service.mall.result.ItemSkuResult;
import com.yfshop.shop.service.merchant.result.MerchantResult;
import com.yfshop.shop.service.merchant.service.FrontMerchantService;
import com.yfshop.shop.service.order.result.YfUserOrderDetailResult;
import com.yfshop.shop.service.order.result.YfUserOrderListResult;
import com.yfshop.shop.service.order.service.FrontUserOrderService;
import com.yfshop.shop.service.user.service.FrontUserService;
import com.yfshop.wx.api.service.MpPayService;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Title:用户订单Service实现
 * @Description:
 * @Author:Wuyc
 * @Since:2021-03-31 16:16:25
 * @Version:1.1.0
 */
@DubboService
public class FrontUserOrderServiceImpl implements FrontUserOrderService {
    private static final Logger logger = LoggerFactory.getLogger(FrontUserOrderServiceImpl.class);
    @Value("${wxPay.notifyUrl}")
    private String wxPayNotifyUrl;
    @Resource
    private OrderDao orderDao;
    @Resource
    private UserMapper userMapper;
    @Resource
    private MallService mallService;
    @Resource
    private OrderMapper orderMapper;
    @DubboReference
    private MpPayService mpPayService;
    @Resource
    private UserCartService userCartService;
    @Resource
    private UserCartMapper userCartMapper;
    @Resource
    private FrontDrawService frontDrawService;
    @Resource
    private FrontUserService frontUserService;
    @Resource
    private UserAddressService userAddressService;
    @Resource
    private UserCouponMapper userCouponMapper;
    @Resource
    private OrderDetailMapper orderDetailMapper;
    @Resource
    private OrderAddressMapper orderAddressMapper;
    @Resource
    private ItemSkuMapper itemSkuMapper;
    @Resource
    private FrontMerchantService frontMerchantService;
    @Resource
    private FrontUserCouponService frontUserCouponService;
    @Resource
    private FrontDrawRecordService frontDrawRecordService;
    @Resource
    private PostageRulesMapper postageRulesMapper;
    @Resource
    private RedisService redisService;
    @Resource
    private ItemSkuMapper skuMapper;
    @Resource
    private ItemMapper itemMapper;
    @Resource
    private CouponRulesMapper couponRulesMapper;
    @Resource
    private CouponMapper couponMapper;
    @Resource
    private DrawRecordMapper drawRecordMapper;
    private final static String drawCanUseRegion = "湖南,湖北,江西,四川,重庆,江苏,浙江,安徽,福建,广东,广西,河南,云南,贵州,山东,陕西,海南,山西,上海";

    /**
     * 校验提交订单的时候是否支持自提
     *
     * @param userId 用户id
     * @param itemId 商品id
     * @return 支持自提，返回true， 否则返回false
     */
    @Override
    public Boolean checkSubmitOrderIsCanZt(Integer userId, Integer itemId, Integer skuId) {
        List<UserCoupon> userCouponList = userCouponMapper.selectList(Wrappers.lambdaQuery(UserCoupon.class)
                .eq(UserCoupon::getUserId, userId)
                .eq(UserCoupon::getDrawPrizeLevel, 2)
                .gt(UserCoupon::getValidEndTime, new Date())
                .eq(UserCoupon::getUseStatus, UserCouponStatusEnum.NO_USE.getCode()));
        if (CollectionUtil.isEmpty(userCouponList)) {
            return false;
        }

        if ("ALL".equalsIgnoreCase(userCouponList.get(0).getUseRangeType()) || userCouponList.get(0).getCanUseItemIds().contains(itemId + "")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 查询用户所有订单, 根据订单状态去组装. 因为单个用户不可能会有很多订单
     * 待付款，已取消状态的订单，主订单信息是order里的数据， 子订单是orderDetail
     * 待发货，待收货，已完成的订单，主订单是detail 里的数据， 子订单是orderDetail
     *
     * @param userId    用户id
     * @param useStatus 订单状态
     * @return
     * @throws ApiException
     */
    @Override
    public List<YfUserOrderListResult> findUserOrderList(Integer userId, String useStatus) throws ApiException {
        Asserts.assertNonNull(userId, 500, "用户id不可以为空");
        // 量大的话可以做10秒缓存
        List<Order> orderList = orderMapper.selectList(Wrappers.lambdaQuery(Order.class)
                .eq(Order::getUserId, userId).orderByDesc(Order::getId));
        List<OrderDetail> detailList = orderDetailMapper.selectList(Wrappers.lambdaQuery(OrderDetail.class)
                .eq(OrderDetail::getUserId, userId).orderByDesc(OrderDetail::getId));
        if (!CollectionUtils.isEmpty(detailList)) {
            List<Integer> skuIds = detailList.stream().map(OrderDetail::getSkuId).distinct().collect(Collectors.toList());
            Map<Integer, BigDecimal> skuMarketPriceMap = itemSkuMapper.selectBatchIds(skuIds).stream().collect(Collectors.toMap(ItemSku::getId, ItemSku::getSkuMarketPrice));
            detailList.forEach(item -> {
                item.setMarketPrice(skuMarketPriceMap.get(item.getSkuId()));
            });
        }
        List<YfUserOrderListResult> dataList = setUserOrderListResult(orderList, detailList);
        if (StringUtils.isBlank(useStatus)) {
            return dataList;
        }
        Map<String, List<YfUserOrderListResult>> dataListMap = dataList.stream().collect(Collectors.groupingBy(YfUserOrderListResult::getOrderStatus));
        return dataListMap.get(useStatus);
    }

    /**
     * 订单id为空说明查的是子订单详情
     * 订单id不为空代表查的是整个订单的详情，一般用于未支付，订单取消的订单
     *
     * @param userId        用户id
     * @param orderId       订单id
     * @param orderDetailId 订单详情id
     * @return
     * @throws ApiException
     */
    @Override
    public YfUserOrderDetailResult getUserOrderDetail(Integer userId, Long orderId, Long orderDetailId) throws ApiException {
        Asserts.assertFalse(orderId == null && orderDetailId == null, 500, "订单标识不可以为空");
        YfUserOrderDetailResult userOrderDetailResult;

        Order order = orderMapper.selectOne(Wrappers.lambdaQuery(Order.class)
                .eq(Order::getUserId, userId)
                .eq(Order::getId, orderId));
        List<OrderDetail> detailList = orderDetailMapper.selectList(Wrappers.lambdaQuery(OrderDetail.class)
                .eq(OrderDetail::getUserId, userId)
                .eq(OrderDetail::getOrderId, orderId)
                .orderByDesc(OrderDetail::getId));
        if (order == null || CollectionUtil.isEmpty(detailList)) {
            return new YfUserOrderDetailResult();
        }
        List<Integer> skuIds = detailList.stream().map(OrderDetail::getSkuId).distinct().collect(Collectors.toList());
        Map<Integer, BigDecimal> skuMarketPriceMap = itemSkuMapper.selectBatchIds(skuIds).stream().collect(Collectors.toMap(ItemSku::getId, ItemSku::getSkuMarketPrice));
        detailList.forEach(item -> {
            item.setMarketPrice(skuMarketPriceMap.get(item.getSkuId()));
        });
        if (orderDetailId == null) {
            List<YfUserOrderDetailResult.YfUserOrderItem> itemList = BeanUtil.convertList(detailList, YfUserOrderDetailResult.YfUserOrderItem.class);
            userOrderDetailResult = BeanUtil.convert(order, YfUserOrderDetailResult.class);
            userOrderDetailResult.setOrderStatus(detailList.get(0).getOrderStatus());
            userOrderDetailResult.setOrderId(order.getId());
            userOrderDetailResult.setItemList(itemList);
        } else {
            List<OrderDetail> itemList = detailList.stream().filter(data -> data.getId().equals(orderDetailId)).collect(Collectors.toList());
            List<YfUserOrderDetailResult.YfUserOrderItem> resultItemList = BeanUtil.convertList(itemList, YfUserOrderDetailResult.YfUserOrderItem.class);
            userOrderDetailResult = BeanUtil.convert(itemList.get(0), YfUserOrderDetailResult.class);
            userOrderDetailResult.setOrderId(order.getId());
            userOrderDetailResult.setOrderDetailId(itemList.get(0).getId());
            userOrderDetailResult.setOrderNo(itemList.get(0).getOrderNo());
            userOrderDetailResult.setOrderStatus(detailList.get(0).getOrderStatus());
            userOrderDetailResult.setItemList(resultItemList);
        }

        // 设置收货地址
        OrderAddress orderAddress = orderAddressMapper.selectOne(Wrappers.lambdaQuery(OrderAddress.class).eq(OrderAddress::getOrderId, orderId));
        YfUserOrderDetailResult.YfUserOrderAddress addressInfo = BeanUtil.convert(orderAddress, YfUserOrderDetailResult.YfUserOrderAddress.class);
        userOrderDetailResult.setAddressInfo(addressInfo);
        userOrderDetailResult.setReceiveWay(order.getReceiveWay());
        return userOrderDetailResult;
    }

    /**
     * 用户取消订单
     *
     * @param userId  用户id
     * @param orderId 订单id
     * @throws ApiException
     */
    @Override
    public Void cancelOrder(Integer userId, Long orderId) throws ApiException {
        Order order = orderMapper.selectOne(Wrappers.lambdaQuery(Order.class)
                .eq(Order::getUserId, userId)
                .eq(Order::getId, orderId));
        Asserts.assertNonNull(order, 500, "主订单信息不存在");
        Asserts.assertNotEquals("Y", order.getIsPay(), 500, "订单已支付,不能取消.");

        // 退还优惠券.优惠券改成未使用
        List<OrderDetail> detailList = orderDetailMapper.selectList(Wrappers.lambdaQuery(OrderDetail.class).eq(OrderDetail::getOrderId, orderId));
        detailList.forEach(orderDetail -> {
            if (orderDetail.getUserCouponId() != null) {
                UserCoupon userCoupon = new UserCoupon();
                userCoupon.setUseStatus(UserCouponStatusEnum.NO_USE.getCode());
                userCoupon.setId(orderDetail.getUserCouponId());
                userCouponMapper.updateById(userCoupon);
                frontDrawRecordService.updateDrawRecordUseStatus(orderDetail.getUserCouponId(), UserCouponStatusEnum.NO_USE.getCode());
            }
            orderDetail.setOrderStatus(UserOrderStatusEnum.CANCEL.getCode());
            orderDetailMapper.updateById(orderDetail);
        });
        order.setIsCancel("Y");
        order.setIsPay("N");
        order.setCancelTime(LocalDateTime.now());
        orderMapper.updateById(order);
        return null;
    }

    /**
     * 用户确认订单
     *
     * @param userId        用户id
     * @param orderDetailId 订单详情id
     * @throws ApiException
     */
    @Override
    public Void confirmOrder(Integer userId, Long orderDetailId) throws ApiException {
        OrderDetail orderDetail = orderDetailMapper.selectOne(Wrappers.lambdaQuery(OrderDetail.class)
                .eq(OrderDetail::getUserId, userId)
                .eq(OrderDetail::getId, orderDetailId));
        Asserts.assertNonNull(orderDetail, 500, "子订单信息不存在");
        Order order = orderMapper.selectOne(Wrappers.lambdaQuery(Order.class)
                .eq(Order::getUserId, userId)
                .eq(Order::getId, orderDetail.getOrderId()));
        Asserts.assertNonNull(order, 500, "主订单信息不存在");
        Asserts.assertNotEquals("N", order.getIsPay(), 500, "订单状态不正确。");

        orderDetail.setOrderStatus("SUCCESS");
        orderDetailMapper.updateById(orderDetail);

        // 优惠券改成已使用
        if (orderDetail.getUserCouponId() != null) {
            UserCoupon userCoupon = new UserCoupon();
            userCoupon.setUseStatus(UserCouponStatusEnum.HAS_USE.getCode());
            userCoupon.setId(orderDetail.getUserCouponId());
            userCouponMapper.updateById(userCoupon);
            frontDrawRecordService.updateDrawRecordUseStatus(orderDetail.getUserCouponId(), UserCouponStatusEnum.HAS_USE.getCode());
        }
        return null;
    }

    /**
     * 商品单个立即购买
     *
     * @param userId       用户id
     * @param skuId        skuId
     * @param num          购买数量
     * @param userCouponId 用户优惠券id
     * @param addressId    用户地址id
     * @return
     * @throws ApiException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> submitOrderBySkuId(Integer userId, Integer skuId, Integer num, Long userCouponId, Long addressId) throws ApiException {
        ItemSkuResult itemSku = mallService.getItemSkuBySkuId(skuId);
        Asserts.assertFalse(itemSku.getSkuStock() < num, 500, "商品库存不足");

        UserAddressResult addressInfo = userAddressService.queryUserAddresses(userId).stream()
                .filter(data -> data.getId().intValue() == addressId).findFirst().orElse(null);
        Asserts.assertNonNull(addressInfo, 500, "收货地址不存在");
        checkPrizeAddress(skuId, addressInfo.getProvince());
        List<UserCartResult> resultList = new ArrayList<>();
        UserCoupon userCoupon = null;
        BigDecimal couponPrice = BigDecimal.ZERO;
        if (userCouponId != null) {
            userCoupon = userCouponMapper.selectById(userCouponId);
            this.checkUserCoupon(userId, userCoupon, itemSku.getItemId());
            couponPrice = couponPrice.add(new BigDecimal(userCoupon.getCouponPrice()));
        }

        BigDecimal orderFreight = new BigDecimal(0);
        // 扣库存, 修改优惠券状态
        mallService.updateItemSkuStock(itemSku.getId(), num);
        if (userCoupon != null) {
            frontUserCouponService.useUserCoupon(userCoupon.getId());
        }
        Item item = itemMapper.selectById(itemSku.getItemId());
        UserCartResult userCartResult = BeanUtil.convert(itemSku, UserCartResult.class);
        userCartResult.setNum(num);
        userCartResult.setCategoryId(itemSku.getCategoryId());
        userCartResult.setSkuType(itemSku.getSkuType());
        userCartResult.setSkuId(skuId);
        userCartResult.setIsAvailable("Y".equals(item.getIsEnable()) && "N".equals(item.getIsDelete()) ? "Y" : "N");
        resultList.add(userCartResult);
        UserCartSummary userCartSummary = calculationSummary(resultList, userCoupon);
        Asserts.assertTrue(userCartSummary.getOrderPrice().longValue() > 0, 500, "支付金额不能小于0元");
        Order order = insertUserOrder(userId, null, ReceiveWayEnum.PS.getCode(), userCartSummary.getItemCount(), userCartSummary.getCarts().size(), userCartSummary.getOrderPrice(), couponPrice, orderFreight, userCartSummary.getPayMoney(), "N", null);
        Long orderId = order.getId();
        if (userCoupon != null) {
            try {
                frontUserCouponService.updateCouponData(userCoupon.getId(), orderId, addressInfo.getMobile());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String userName = null;
        User user = userMapper.selectById(userId);
        if (user != null) {
            userName = user.getNickname();
        }
        for (UserCartResult userCart : userCartSummary.getCarts()) {
            insertUserOrderDetail(userId, userName, orderId, null, null, null, ReceiveWayEnum.PS.getCode(), "N", userCart.getNum(),
                    userCart.getItemId(), userCart.getSkuId(), userCart.getSkuTitle(), userCart.getSkuSalePrice(), userCart.getSkuCover(), userCart.getFreight(), userCart.getCouponPrice(),
                    userCart.getOrderPrice(), userCart.getPayPrice(), userCart.getUserCouponId(), UserOrderStatusEnum.WAIT_PAY.getCode(), userCart.getSpecValueIdPath(), userCart.getSpecNameValueJson());
        }
        insertUserOrderAddress(orderId, addressInfo.getMobile(), addressInfo.getRealname(), addressInfo.getProvince(), addressInfo.getProvinceId(), addressInfo.getCity(),
                addressInfo.getCityId(), addressInfo.getDistrict(), addressInfo.getDistrictId(), addressInfo.getAddress());
        Map<String, Object> resultMap = new HashMap<>(4);
        resultMap.put("orderId", orderId);
        resultMap.put("isPay", "N");
        return resultMap;
    }

    /**
     * 商品购物车下单购买
     *
     * @param userId       用户id
     * @param cartIds      购物车id
     * @param userCouponId 用户优惠券id
     * @param addressId    用户地址id
     * @return
     * @throws ApiException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> submitOrderByCart(Integer userId, String cartIds, Long userCouponId, Long addressId) throws ApiException {
        List<Integer> cartIdList = Arrays.stream(StringUtils.split(cartIds, ",")).map(Integer::valueOf)
                .collect(Collectors.toList());
        Asserts.assertCollectionNotEmpty(cartIdList, 500, "购物车id不可以为空");
        Asserts.assertFalse(cartIdList.size() > 1 && userCouponId != null, 500, "您不能使用优惠券");

        UserAddressResult addressInfo = userAddressService.queryUserAddresses(userId).stream()
                .filter(data -> data.getId().intValue() == addressId).findFirst().orElse(null);
        Asserts.assertNonNull(addressInfo, 500, "收货地址不存在");

        List<UserCart> userCartList = userCartMapper.selectList(Wrappers.lambdaQuery(UserCart.class)
                .eq(UserCart::getUserId, userId).in(UserCart::getId, cartIdList));
        Asserts.assertCollectionNotEmpty(userCartList, 500, "购物车id不正确");
        Asserts.assertEquals(userCartList.size(), cartIdList.size(), 500, "购物车数据不正确，请刷新重试");

        UserCoupon userCoupon = null;
        BigDecimal couponPrice = BigDecimal.ZERO;
        if (userCouponId != null) {
            userCoupon = userCouponMapper.selectById(userCouponId);
            this.checkUserCoupon(userId, userCoupon, userCartList.get(0).getItemId());
            couponPrice = couponPrice.add(new BigDecimal(userCoupon.getCouponPrice()));
        }
        List<UserCartResult> resultList = BeanUtil.convertList(userCartList, UserCartResult.class);
        List<Integer> skuIdList = userCartList.stream().map(UserCart::getSkuId).collect(Collectors.toList());
        List<Integer> itemIdList = userCartList.stream().map(UserCart::getItemId).collect(Collectors.toList());
        Map<Integer, Item> itemIndexMap = itemMapper.selectBatchIds(itemIdList).stream().collect(Collectors.toMap(Item::getId, s -> s));
        Map<Integer, ItemSku> skuIndexMap = skuMapper.selectBatchIds(skuIdList).stream().collect(Collectors.toMap(ItemSku::getId, s -> s));
        for (UserCartResult userCart : resultList) {
            ItemSku itemSku = skuIndexMap.get(userCart.getSkuId());
            Item item = itemIndexMap.get(userCart.getItemId());
            Integer id = userCart.getNum();
            BeanUtil.copyProperties(itemSku, userCart);
            userCart.setId(id);
            Asserts.assertFalse(itemSku.getSkuStock() < userCart.getNum(), 500, "商品库存不足");
            checkPrizeAddress(userCart.getSkuId(), addressInfo.getProvince());
            // 扣库存, 修改优惠券状态
            mallService.updateItemSkuStock(itemSku.getId(), userCart.getNum());
            userCart.setFreight(itemSku.getFreight());
            userCart.setSkuSalePrice(itemSku.getSkuSalePrice());
            userCart.setCategoryId(itemSku.getCategoryId());
            userCart.setSkuType(itemSku.getSkuType());
            userCart.setIsAvailable("Y".equals(item.getIsEnable()) && "N".equals(item.getIsDelete()) ? "Y" : "N");
        }
        if (userCoupon != null) {
            frontUserCouponService.useUserCoupon(userCoupon.getId());
        }
        UserCartSummary userCartSummary = calculationSummary(resultList, userCoupon);
        Asserts.assertTrue(userCartSummary.getOrderPrice().longValue() > 0, 500, "支付金额不能小于0元");
        //删除购物车id
        userCartService.deleteUserCarts(userId, skuIdList);
        Order order = insertUserOrder(userId, null, ReceiveWayEnum.PS.getCode(), userCartSummary.getItemCount(), userCartSummary.getCarts().size(), userCartSummary.getOrderPrice(), couponPrice, userCartSummary.getTotalFreight(), userCartSummary.getPayMoney(), "N", null);
        Long orderId = order.getId();
        if (userCoupon != null) {
            try {
                frontUserCouponService.updateCouponData(userCoupon.getId(), orderId, addressInfo.getMobile());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String userName = null;
        User user = userMapper.selectById(userId);
        if (user != null) {
            userName = user.getNickname();
        }
        for (UserCartResult userCart : userCartSummary.getCarts()) {
            insertUserOrderDetail(userId, userName, orderId, null, null, null, ReceiveWayEnum.PS.getCode(), "N", userCart.getNum(),
                    userCart.getItemId(), userCart.getSkuId(), userCart.getSkuTitle(), userCart.getSkuSalePrice(), userCart.getSkuCover(), userCart.getFreight(), userCart.getCouponPrice(),
                    userCart.getOrderPrice(), userCart.getPayPrice(), userCart.getUserCouponId(), UserOrderStatusEnum.WAIT_PAY.getCode(), userCart.getSpecValueIdPath(), userCart.getSpecNameValueJson());
        }
        insertUserOrderAddress(orderId, addressInfo.getMobile(), addressInfo.getRealname(), addressInfo.getProvince(), addressInfo.getProvinceId(), addressInfo.getCity(),
                addressInfo.getCityId(), addressInfo.getDistrict(), addressInfo.getDistrictId(), addressInfo.getAddress());

        Map<String, Object> resultMap = new HashMap<>(4);
        resultMap.put("orderId", orderId);
        resultMap.put("isPay", "N");
        return resultMap;
    }

    /**
     * 优惠券购买商品
     *
     * @param userId        用户id
     * @param userCouponIds 用户优惠券ids(只有二等奖可以自提)
     * @param userMobile    用户手机号
     * @param websiteCode   商户网点码
     * @return
     * @throws ApiException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> submitOrderByUserCouponIds(Integer userId, String userCouponIds, String userMobile, String websiteCode) throws ApiException {
        // 校验网点码商户
        MerchantResult merchantResult = frontMerchantService.getMerchantByWebsiteCode(websiteCode);

        // 校验用户优惠券
        List<Long> userCouponIdList = Arrays.stream(StringUtils.split(userCouponIds, ","))
                .map(Long::valueOf).collect(Collectors.toList());
        Asserts.assertCollectionNotEmpty(userCouponIdList, 500, "用户优惠券id不可以为空");
        List<UserCoupon> userCouponList = userCouponMapper.selectList(Wrappers.lambdaQuery(UserCoupon.class)
                .in(UserCoupon::getId, userCouponIdList));
        // 二等奖是一个商品，这个sku必须是单规格
        List<Integer> itemIdList = userCouponList.stream().map(UserCoupon::getCanUseItemIds).map(Integer::valueOf).collect(Collectors.toList());
        Asserts.assertCollectionNotEmpty(userCouponList, 500, "用户优惠券查询不到");
        Map<Long, List<UserCoupon>> userCouponMap = userCouponList.stream().collect(Collectors.groupingBy(UserCoupon::getId));
        for (Long userCouponId : userCouponIdList) {
            List<UserCoupon> dataList = userCouponMap.get(userCouponId);
            Asserts.assertCollectionNotEmpty(dataList, 500, "用户优惠券不存在");
            this.checkUserCoupon(userId, dataList.get(0), itemIdList.get(0));
        }

        // 校验抽奖活动,当前有且仅有一个活动进行中
        Set<Integer> actIdSetList = userCouponList.stream().map(UserCoupon::getDrawActivityId).collect(Collectors.toSet());
        Asserts.assertFalse(actIdSetList.size() > 1, 500, "请选择正确的活动");
        YfDrawActivityResult drawActivityResult = frontDrawService.getDrawActivityDetailById(actIdSetList.iterator().next());
        Asserts.assertNonNull(drawActivityResult, 500, "此活动不存在,请联系管理员处理");
        Set<Integer> couponIdSetList = userCouponList.stream().map(UserCoupon::getCouponId).collect(Collectors.toSet());
        Asserts.assertFalse(couponIdSetList.size() > 1, 500, "请传入正确的用户优惠券,自提奖品只支持二等奖");
        YfDrawPrizeResult yfDrawPrizeResult = drawActivityResult.getPrizeList().stream().filter(prize ->
                prize.getPrizeLevel() == 2).collect(Collectors.toList()).get(0);
        Asserts.assertTrue(userCouponList.get(0).getCouponId().intValue() == yfDrawPrizeResult.getCouponId().intValue(),
                500, "自提奖品只支持二等奖");

        QueryItemDetailReq req = new QueryItemDetailReq();
        req.setItemId(itemIdList.get(0));
        ItemResult itemDetail = mallService.findItemDetail(req);
        ItemSkuResult itemSku = itemDetail.getItemSkuList().get(0);

        // 修改优惠券状态
        userCouponIdList.forEach(userCouponId -> {
            frontUserCouponService.useUserCoupon(userCouponId);
        });

        // 扣优惠券对应的商品库存
        mallService.updateItemSkuStock(itemSku.getId(), userCouponIdList.size());

        // 根据优惠券计算订单金额，创建订单,子订单, 收货地址 一个优惠券对应一个子订单，一个子订单运费2块钱
        Integer itemCount = userCouponIdList.size();
        BigDecimal orderFreight = new BigDecimal("0");
        BigDecimal orderCouponPrice = new BigDecimal(userCouponList.get(0).getCouponPrice() * userCouponList.size());
        BigDecimal orderPayPrice = new BigDecimal(itemCount).multiply(new BigDecimal("2"));
        BigDecimal orderPrice = new BigDecimal(itemCount).multiply(itemSku.getSkuSalePrice()).setScale(2, BigDecimal.ROUND_UP);

        Order order = insertUserOrder(userId, websiteCode, ReceiveWayEnum.ZT.getCode(), itemCount, itemCount, orderPrice, orderCouponPrice, orderFreight, orderPayPrice, "N", null);
        String userName = null;
        User user = userMapper.selectById(userId);
        if (user != null) {
            userName = user.getNickname();
        }
        Long orderId = order.getId();
        for (UserCoupon userCoupon : userCouponList) {
            try {
                frontUserCouponService.updateCouponData(userCoupon.getId(), orderId, userMobile);
            } catch (Exception e) {
                e.printStackTrace();
            }
            BigDecimal couponPrice = new BigDecimal(userCoupon.getCouponPrice());
            BigDecimal payPrice = itemSku.getSkuSalePrice().subtract(couponPrice);
            insertUserOrderDetail(userId, userName, orderId, merchantResult.getId(), merchantResult.getPidPath(), websiteCode, ReceiveWayEnum.ZT.getCode(), "N", 1,
                    itemSku.getItemId(), itemSku.getId(), itemDetail.getItemTitle(), itemSku.getSkuSalePrice(), itemSku.getSkuCover(), orderFreight, couponPrice,
                    itemSku.getSkuSalePrice(), payPrice, userCoupon.getId(), UserOrderStatusEnum.WAIT_PAY.getCode(), itemSku.getSpecValueIdPath(), itemSku.getSpecNameValueJson());
        }
        insertUserOrderAddress(orderId, userMobile, userMobile, merchantResult.getProvince(), merchantResult.getProvinceId(), merchantResult.getCity(),
                merchantResult.getCityId(), merchantResult.getDistrict(), merchantResult.getDistrictId(), merchantResult.getAddress());

        Map<String, Object> resultMap = new HashMap<>(4);
        resultMap.put("orderId", orderId);
        resultMap.put("isPay", "N");
        return resultMap;
    }

    /**
     * 用户付款后修改订单状态
     *
     * @param orderId 主订单id
     * @return
     * @throws ApiException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Void updateOrderPayStatus(Long orderId) throws ApiException {
        Asserts.assertNonNull(orderId, 500, "主订单id不可以为空");
        Order order = orderMapper.selectById(orderId);
        Asserts.assertNonNull(order, 500, "订单不存在");
        Asserts.assertNotEquals(order.getIsPay(), "Y", 500, "订单不可以重复修改状态");

        // 修改订单状态，用乐观锁
        int result = orderDao.updateOrderPayStatus(orderId);
        if (result <= 0) {
            Asserts.assertNotEquals(order.getIsPay(), "Y", 500, "订单不可以重复修改状态");
        }
        frontMerchantService.insertWebsiteBill(orderId);
        return null;
    }

    /**
     * 根据订单号唤起微信支付
     *
     * @param orderId 用户订单id
     * @param ipStr   请求ip地址
     * @return WxPayMpOrderResult
     * @throws ApiException
     */
    @Override
    public WxPayMpOrderResult userOrderToPay(Long orderId, String ipStr) throws WxPayException, ApiException {
        Asserts.assertNonNull(orderId, 500, "主订单id不可以为空");
        Order order = orderMapper.selectById(orderId);
        Asserts.assertNonNull(order, 500, "订单不存在");
        Asserts.assertNotEquals(order.getIsPay(), "Y", 500, "订单已支付");
        User user = userMapper.selectById(order.getUserId());

        WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
        orderRequest.setBody("用户订单支付");
        orderRequest.setTradeType("JSAPI");
        orderRequest.setOpenid(user.getOpenId());
        orderRequest.setSpbillCreateIp(ipStr);
        orderRequest.setNotifyUrl(wxPayNotifyUrl + PayPrefixEnum.USER_ORDER.getBizType());
        orderRequest.setTotalFee(BaseWxPayRequest.yuanToFen(order.getPayPrice().toString()));
        orderRequest.setTimeStart(DateFormatUtils.format(new Date(), "yyyyMMddHHmmss"));
        orderRequest.setOutTradeNo(PayPrefixEnum.USER_ORDER.getPrefix() + order.getId() + "-" + order.getPayEntryCount());
        orderRequest.setTimeExpire(DateFormatUtils.format(new Date(System.currentTimeMillis() + (1000 * 60 * 15)), "yyyyMMddHHmmss"));
        if ("pro".equalsIgnoreCase(SpringUtil.getActiveProfile())) {
            WxPayMpOrderResult payOrderResult = mpPayService.createPayOrder(orderRequest);
            // 修改订单支付重试次数, 防止唤起支付后不立马支付
            orderDao.updateOrderPayEntryCount(orderId);
            Order o = new Order();
            o.setId(orderId);
            o.setOutOrderNo(orderRequest.getOutTradeNo());
            orderMapper.updateById(o);
            logger.debug("唤起订单{},支付->{}", orderId, order.getPayPrice().toString());
            return payOrderResult;
        } else {
            // 修改订单支付重试次数, 防止唤起支付后不立马支付
            orderDao.updateOrderPayEntryCount(orderId);
            Order o = new Order();
            o.setId(orderId);
            o.setOutOrderNo(orderRequest.getOutTradeNo());
            orderMapper.updateById(o);
            logger.debug("唤起订单{},支付->{}", orderId, order.getPayPrice().toString());
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("orderId", orderId);
            paramMap.put("billNo", RandomUtil.randomNumbers(36));
            HttpUtil.post("https://prev-upms.yufanlook.com/admin/order/testOrder", paramMap);
            return new WxPayMpOrderResult();
        }

    }


    @Override
    public Void userOrderCancelPay(Long orderId) throws ApiException {
        orderDao.orderCancelPay(orderId);
        return null;
    }

    //----------------------------------------------------- private method ---------------------------------------------------------------------------------

    /**
     * 组装前台c端用户展示的通用订单数据
     *
     * @param orderList 订单列表
     * @param childList 子订单列表
     * @return
     */
    private List<YfUserOrderListResult> setUserOrderListResult(List<Order> orderList, List<OrderDetail> childList) {
        List<YfUserOrderListResult> resultList = new ArrayList<>();
        for (Order order : orderList) {
            YfUserOrderListResult orderResult;
            List<OrderDetail> detailList = childList.stream().filter(data -> data.getOrderId().equals(order.getId())).collect(Collectors.toList());
            if (!"Y".equalsIgnoreCase(order.getIsPay()) && "N".equalsIgnoreCase(order.getIsCancel())) {
                // 未付款订单状态组装
                orderResult = BeanUtil.convert(order, YfUserOrderListResult.class);
                orderResult.setOrderId(order.getId());
                orderResult.setOrderStatus(UserOrderStatusEnum.WAIT_PAY.getCode());
                orderResult.setItemList(BeanUtil.convertList(detailList, YfUserOrderListResult.YfUserOrderItem.class));
                resultList.add(orderResult);
            } else if (!"Y".equalsIgnoreCase(order.getIsPay()) && "Y".equalsIgnoreCase(order.getIsCancel())) {
                // 已取消订单状态组装
                orderResult = BeanUtil.convert(order, YfUserOrderListResult.class);
                orderResult.setOrderId(order.getId());
                orderResult.setOrderStatus(UserOrderStatusEnum.CANCEL.getCode());
                orderResult.setItemList(BeanUtil.convertList(detailList, YfUserOrderListResult.YfUserOrderItem.class));
                resultList.add(orderResult);
            } else {
                // 待发货，待收货，已完成订单状态组装数据
                OrderAddress orderAddress = orderAddressMapper.selectOne(Wrappers.lambdaQuery(OrderAddress.class).eq(OrderAddress::getOrderId, order.getId()));
                String mobile = orderAddress == null ? "" : orderAddress.getMobile();
                for (OrderDetail orderDetail : detailList) {
                    orderResult = BeanUtil.convert(orderDetail, YfUserOrderListResult.class);
                    orderResult.setOrderDetailId(orderDetail.getId());
                    orderResult.setOrderNo(orderDetail.getOrderNo());
                    orderResult.setMobile(mobile);
                    orderResult.setItemList(BeanUtil.convertList(Arrays.asList(orderDetail), YfUserOrderListResult.YfUserOrderItem.class));
                    resultList.add(orderResult);
                }
            }
        }
        return resultList;
    }


    /**
     * 创建用户订单
     *
     * @param userId          用户id
     * @param receiveWay      收货方式 ZT | PS
     * @param itemCount       商品数量
     * @param childOrderCount 子订单数量
     * @param orderPrice      订单金额
     * @param couponPrice     优惠券金额
     * @param freight         运费
     * @param payPrice        实际支付金额
     * @param isPay           是否支付
     * @param remark
     * @return Order
     */
    private Order insertUserOrder(Integer userId, String webSiteCode, String receiveWay, Integer itemCount, Integer childOrderCount, BigDecimal orderPrice,
                                  BigDecimal couponPrice, BigDecimal freight, BigDecimal payPrice, String isPay, String remark) {
        Order order = new Order();
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        order.setUserId(userId);
        order.setWebsiteCode(webSiteCode);
        order.setReceiveWay(receiveWay);
        order.setItemCount(itemCount);
        order.setChildOrderCount(childOrderCount);
        order.setOrderPrice(orderPrice);
        order.setCouponPrice(couponPrice);
        order.setFreight(freight);
        order.setPayPrice(payPrice);
        order.setIsPay(isPay);
        order.setBillNo(null);
        order.setPayTime(null);
        order.setIsCancel("N");
        order.setCancelTime(null);
        order.setPayEntryCount(1);
        order.setRemark(remark);
        orderMapper.insert(order);
        return order;
    }

    /**
     * 创建订单详情
     *
     * @param userId            用户id
     * @param orderId           订单id
     * @param merchantId        商户id
     * @param pidPath           商户pidPath
     * @param receiveWay        收货方式 ZT | PS
     * @param isPay             是否支付
     * @param itemCount         商品数量
     * @param itemId            商品id
     * @param skuId             skuId
     * @param itemTitle         商品标题
     * @param skuPrice          商品sku售价
     * @param itemCover         商品图片
     * @param freight           运费
     * @param couponPrice       优惠金额
     * @param orderPrice        订单金额
     * @param payPrice          实际支付金额
     * @param userCouponId      用户优惠券id
     * @param orderStatus       订单状态
     * @param specValueIdPath   商品sku的specValueIdPath
     * @param specNameValueJson 商品sku的规格名称值json串
     * @return
     */
    private OrderDetail insertUserOrderDetail(Integer userId, String userName, Long orderId, Integer merchantId, String pidPath, String websiteCode, String receiveWay,
                                              String isPay, Integer itemCount, Integer itemId, Integer skuId, String itemTitle, BigDecimal skuPrice,
                                              String itemCover, BigDecimal freight, BigDecimal couponPrice, BigDecimal orderPrice, BigDecimal payPrice,
                                              Long userCouponId, String orderStatus, String specValueIdPath, String specNameValueJson) {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setCreateTime(LocalDateTime.now());
        orderDetail.setUserId(userId);
        orderDetail.setOrderId(orderId);
        orderDetail.setMerchantId(merchantId);
        orderDetail.setPidPath(pidPath);
        orderDetail.setWebsiteCode(websiteCode);
        orderDetail.setReceiveWay(receiveWay);
        orderDetail.setIsPay(isPay);
        orderDetail.setItemId(itemId);
        orderDetail.setSkuId(skuId);
        orderDetail.setItemCover(itemCover);
        orderDetail.setItemPrice(skuPrice);
        orderDetail.setItemCount(itemCount);
        orderDetail.setFreight(freight);
        orderDetail.setCouponPrice(couponPrice);
        orderDetail.setOrderPrice(orderPrice);
        orderDetail.setPayPrice(payPrice);
        orderDetail.setUserCouponId(userCouponId);
        orderDetail.setOrderStatus(orderStatus);
        orderDetail.setItemTitle(itemTitle);
        orderDetail.setSpecValueIdPath(specValueIdPath);
        orderDetail.setSpecNameValueJson(specNameValueJson);
        orderDetail.setSpecValueStr(null);
        orderDetail.setConfirmTime(null);
        orderDetail.setShipTime(null);
        orderDetail.setExpressCompany(null);
        orderDetail.setExpressNo(null);
        orderDetail.setUserName(userName);
        String dataStr = DateUtil.format(LocalDateTime.now(), "yyyyMMdd");
        Long orderCount = redisService.incr(CacheConstants.ORDER_DATE_COUNT + dataStr, 1, 1, TimeUnit.DAYS);
        orderDetail.setOrderNo(String.format("%s%04d", DateFormatUtils.format(new Date(), "yyMMddHHmmss"), orderCount % 10000));
        orderDetailMapper.insert(orderDetail);
        //添加购买记录假的
        addBugRecord(itemId, itemCount);
        return orderDetail;
    }

    /**
     * 创建订单收货地址
     *
     * @param orderId    订单id
     * @param mobile     手机号
     * @param realname   收货人姓名
     * @param province   省份
     * @param provinceId 省id
     * @param city       市
     * @param cityId     市id
     * @param district   区
     * @param districtId 区id
     * @param address    详细地址
     * @return OrderAddress
     */
    private OrderAddress insertUserOrderAddress(Long orderId, String mobile, String realname, String province, Integer provinceId,
                                                String city, Integer cityId, String district, Integer districtId, String address) {
        OrderAddress orderAddress = new OrderAddress();
        orderAddress.setCreateTime(LocalDateTime.now());
        orderAddress.setOrderId(orderId);
        orderAddress.setMobile(mobile);
        orderAddress.setRealname(realname);
        orderAddress.setProvince(province);
        orderAddress.setProvinceId(provinceId);
        orderAddress.setCity(city);
        orderAddress.setCityId(cityId);
        orderAddress.setDistrict(district);
        orderAddress.setDistrictId(districtId);
        orderAddress.setAddress(address);
        orderAddressMapper.insert(orderAddress);
        return orderAddress;
    }

    /**
     * 校验优惠券
     *
     * @param userCoupon
     * @throws ApiException
     */
    private void checkUserCoupon(Integer userId, UserCoupon userCoupon, Integer itemId) throws ApiException {
        Asserts.assertNonNull(userCoupon, 500, "用户优惠券不存在");
        Asserts.assertTrue(userCoupon.getUserId().equals(userId), 500, "优惠券不合法");
        Asserts.assertFalse(userCoupon.getValidEndTime().isBefore(LocalDateTime.now()), 500, "优惠券已过期");
        Asserts.assertEquals(userCoupon.getUseStatus(), UserCouponStatusEnum.NO_USE.getCode(), 500, "优惠券状态不正确");
        Asserts.assertTrue("ALL".equalsIgnoreCase(userCoupon.getUseRangeType()) ||
                userCoupon.getCanUseItemIds().contains(itemId + ""), 500, "请使用正确的优惠券");
    }

    private void checkPrizeAddress(Integer skuId, String provinceName) throws ApiException {
        if (skuId.equals(2030001) || skuId.equals(2032001)) {
            if (!drawCanUseRegion.contains(provinceName.substring(0, 2))) {
                throw new ApiException(500, provinceName + "暂不支持配送一等奖或二等奖");
            }
        }
    }


    private UserCartSummary calculationSummary(List<UserCartResult> userCartResult, UserCoupon userCoupon) {
        logger.debug("结算前商品->{},优惠券使用->{}", userCartResult.toString(), userCoupon != null ? userCoupon.toString() : "");
        List<UserCartResult> allCardList = new ArrayList<>();
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
                int count = item.getNum();
                if (couponPostageRule != null && couponPostageRule.getSkuIds().contains(item.getSkuId() + "")) {
                    userCartSummary.setOrderPrice(userCartSummary.getOrderPrice().add(item.getSkuSalePrice()));
                    userCartSummary.setExchangeMoney(couponPostageRule.getExchangeFee());
                    userCartSummary.setTotalFreight(couponPostageRule.getIsTrue());
                    userCartSummary.setItemCount(userCartSummary.getItemCount() + 1);
                    UserCartResult child = BeanUtil.convert(item, UserCartResult.class);
                    child.setNum(1);
                    child.setCouponPrice(new BigDecimal(userCoupon.getCouponPrice()));
                    child.setOrderPrice(item.getSkuSalePrice().subtract(new BigDecimal(userCoupon.getCouponPrice())));
                    child.setPayPrice(child.getOrderPrice().add(couponPostageRule.getIsTrue()));
                    child.setFreight(couponPostageRule.getIsTrue());
                    child.setUserCouponId(userCoupon.getId());
                    allCardList.add(child);
                    count = count - 1;
                    item.setNum(count);
                    couponPostageRule = null;
                }
                //正常情况的邮费计算
                if (count > 0) {
                    userCartSummary.setItemCount(userCartSummary.getItemCount() + count);
                    userCartSummary.setOrderPrice(userCartSummary.getOrderPrice().add(item.getSkuSalePrice().multiply(new BigDecimal(count))));
                    userCartSummary.setPayMoney(userCartSummary.getPayMoney().add(item.getSkuSalePrice().multiply(new BigDecimal(count))));
                    PostageRules postageRules = postageRulesMapper.selectOne(Wrappers.lambdaQuery(PostageRules.class).apply("FIND_IN_SET({0},sku_ids)  and coupon_id is null", item.getSkuId()));
                    if (postageRules != null) {
                        postageRulesMap.put(postageRules.getId(), postageRules);
                        List<UserCartResult> cartResults = childItemList.get(postageRules.getId());
                        if (cartResults == null) {
                            cartResults = new ArrayList<>();
                            childItemList.put(postageRules.getId(), cartResults);
                        }
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
            //排除是火锅套餐
            if (!(category == 3 && !tcCategory.isEmpty())) {
                //总数
                int sum = 0;
                BigDecimal freight = null;
                for (UserCartResult cartResult : childItem) {
                    sum += cartResult.getNum();
                    pay = pay.add(cartResult.getSkuSalePrice().multiply(new BigDecimal(cartResult.getNum())));
                }
                if (pay.compareTo(value.getConditions()) >= 0) {
                    freight = value.getIsTrue().divide(new BigDecimal(sum), 2, RoundingMode.HALF_UP);
                    userCartSummary.setTotalFreight(userCartSummary.getTotalFreight().add(value.getIsTrue()));
                } else {
                    freight = value.getIsFalse().divide(new BigDecimal(sum), 2, RoundingMode.HALF_UP);
                    userCartSummary.setTotalFreight(userCartSummary.getTotalFreight().add(value.getIsFalse()));
                }
                for (UserCartResult cartResult : childItem) {
                    UserCartResult child = BeanUtil.convert(cartResult, UserCartResult.class);
                    child.setCouponPrice(BigDecimal.ZERO);
                    child.setFreight(new BigDecimal(cartResult.getNum()).multiply(freight));
                    child.setOrderPrice(child.getSkuSalePrice().multiply(new BigDecimal(cartResult.getNum())));
                    child.setPayPrice(child.getOrderPrice().add(child.getFreight()));
                    allCardList.add(child);
                }
            } else {
                for (UserCartResult cartResult : childItem) {
                    UserCartResult child = BeanUtil.convert(cartResult, UserCartResult.class);
                    child.setCouponPrice(BigDecimal.ZERO);
                    child.setFreight(BigDecimal.ZERO);
                    child.setOrderPrice(child.getSkuSalePrice().multiply(new BigDecimal(cartResult.getNum())));
                    child.setPayPrice(child.getOrderPrice());
                    allCardList.add(child);
                }
            }
        });
        userCartSummary.setPayMoney(userCartSummary.getPayMoney().add(userCartSummary.getTotalFreight()));
        userCartSummary.setCarts(allCardList);
        logger.debug("结算后商品->{},优惠券使用->{}", allCardList.toString(), userCoupon != null ? userCoupon.toString() : "");
        return userCartSummary;
    }


    @Override
    public YfUserCouponResult getOrderCoupon(Integer userId, Long orderId) {
        YfUserCouponResult yfUserCouponResult = new YfUserCouponResult();
        yfUserCouponResult.setIsEnable("N");
        UserCoupon userCoupon = userCouponMapper.selectOne(Wrappers.lambdaQuery(UserCoupon.class)
                .eq(UserCoupon::getUserId, userId)
                .eq(UserCoupon::getSrcOrderId, orderId));
        if (userCoupon != null) {
            yfUserCouponResult = BeanUtil.convert(userCoupon, YfUserCouponResult.class);
            yfUserCouponResult.setIsEnable("Y");
        }
        return yfUserCouponResult;
    }

    public void addBugRecord(Integer itemId, int count) {
        try {
            redisService.incr("BuyGoods:" + itemId, count);
        } catch (Exception e) {

        }
    }
}

