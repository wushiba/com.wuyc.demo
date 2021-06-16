package com.yfshop.shop.service.order;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.extra.spring.SpringUtil;
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
import com.yfshop.shop.service.coupon.FrontUserCouponServiceImpl;
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

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
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
    private FrontMerchantService frontMerchantService;
    @Resource
    private FrontUserCouponService frontUserCouponService;
    @Resource
    private FrontDrawRecordService frontDrawRecordService;

    @Resource
    private RedisService redisService;

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
            userOrderDetailResult.setOrderStatus(detailList.get(0).getOrderStatus());
            userOrderDetailResult.setItemList(resultItemList);
        }

        // 设置收货地址
        OrderAddress orderAddress = orderAddressMapper.selectOne(Wrappers.lambdaQuery(OrderAddress.class).eq(OrderAddress::getOrderId, orderId));
        YfUserOrderDetailResult.YfUserOrderAddress addressInfo = BeanUtil.convert(orderAddress, YfUserOrderDetailResult.YfUserOrderAddress.class);
        userOrderDetailResult.setAddressInfo(addressInfo);
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
        Asserts.assertEquals("TC", itemSku.getSkuType(), 500, "该商品不能单独购买，请添加同类套餐一起购买");
        Asserts.assertFalse(itemSku.getSkuStock() < num, 500, "商品库存不足");

        UserAddressResult addressInfo = userAddressService.queryUserAddresses(userId).stream()
                .filter(data -> data.getId().intValue() == addressId).findFirst().orElse(null);
        Asserts.assertNonNull(addressInfo, 500, "收货地址不存在");
        checkPrizeAddress(skuId, addressInfo.getProvince());

        UserCoupon userCoupon = new UserCoupon();
        if (userCouponId != null) {
            userCoupon = userCouponMapper.selectById(userCouponId);
            this.checkUserCoupon(userCoupon, itemSku.getItemId());
        }
        int sum = 0;
        if (itemSku.getCategoryId() != 3) {
            sum = num;
        }
        BigDecimal orderFreight = new BigDecimal(0);
        // 扣库存, 修改优惠券状态
        mallService.updateItemSkuStock(itemSku.getId(), num);
        if (userCoupon.getId() != null) {
            frontUserCouponService.useUserCoupon(userCoupon.getId());
            if (userCoupon.getCanUseItemIds().contains("2032")) {
                orderFreight = orderFreight.add(new BigDecimal("1.8"));
                sum = sum - 1;
            } else if (userCoupon.getCanUseItemIds().contains("2030")) {
                orderFreight = orderFreight.add(new BigDecimal("18"));
                sum = sum - 1;
            }
        }
        // 下单，创建订单，订单详情，收货地址
        //BigDecimal orderFreight = new BigDecimal(num).multiply(itemSku.getFreight());
        BigDecimal orderPrice = new BigDecimal(num).multiply(itemSku.getSkuSalePrice());
        logger.info("订单价格={}", orderPrice.longValue());
        BigDecimal couponPrice = userCoupon.getCouponPrice() == null ? new BigDecimal("0.00") : new BigDecimal(userCoupon.getCouponPrice());
        logger.info("优惠券抵扣={}", couponPrice.longValue());
        BigDecimal payPrice = orderPrice.subtract(couponPrice);
        logger.info("减扣后价格={}", payPrice.longValue());
        //平均运费价格
        BigDecimal freight = BigDecimal.ZERO;
        if (payPrice.longValue() < 88) {
            freight = new BigDecimal("10");
            if (sum > 0) {
                orderFreight = orderFreight.add(new BigDecimal("10"));
                freight = freight.divide(new BigDecimal(sum), 2, BigDecimal.ROUND_HALF_UP);
            }
        }
        logger.info("运费价格={}", orderFreight.longValue());
        payPrice = payPrice.add(orderFreight);
        logger.info("支付订单价格={}", payPrice.longValue());
        Order order = insertUserOrder(userId, null, ReceiveWayEnum.PS.getCode(), num, 1, orderPrice, couponPrice, orderFreight, payPrice, "N", null);
        Long orderId = order.getId();
        String userName = null;
        User user = userMapper.selectById(userId);
        if (user != null) {
            userName = user.getNickname();
        }
        //二等奖数量大于1时，使用优惠券必须拆单为一瓶装
        if ((itemSku.getId().equals(2032001) || itemSku.getId().equals(2030001)) && userCoupon.getId() != null && num > 1) {
            couponPrice = userCoupon.getCouponPrice() == null ? new BigDecimal("0.00") : new BigDecimal(userCoupon.getCouponPrice());
            payPrice = itemSku.getSkuSalePrice().add(itemSku.getFreight()).subtract(couponPrice);
            insertUserOrderDetail(userId, userName, orderId, null, null, null, ReceiveWayEnum.PS.getCode(), "N", 1,
                    itemSku.getItemId(), itemSku.getId(), itemSku.getSkuTitle(), itemSku.getSkuSalePrice(), itemSku.getSkuCover(), itemSku.getFreight(), couponPrice,
                    itemSku.getSkuSalePrice(), payPrice, userCouponId, UserOrderStatusEnum.WAIT_PAY.getCode(), itemSku.getSpecValueIdPath(), itemSku.getSpecNameValueJson());

            num = num - 1;
            userCoupon.setCouponId(null);
            userCoupon.setId(null);
            userCoupon.setCouponPrice(null);
            orderFreight = new BigDecimal(num).multiply(freight);
            orderPrice = new BigDecimal(num).multiply(itemSku.getSkuSalePrice());
            couponPrice = userCoupon.getCouponPrice() == null ? new BigDecimal("0.00") : new BigDecimal(userCoupon.getCouponPrice());
            payPrice = orderPrice.add(orderFreight).subtract(couponPrice);
        } else if ((itemSku.getId().equals(2032001) || itemSku.getId().equals(2030001)) && userCoupon.getId() != null) {
            orderFreight = itemSku.getFreight();
            orderPrice = new BigDecimal(num).multiply(itemSku.getSkuSalePrice());
            couponPrice = userCoupon.getCouponPrice() == null ? new BigDecimal("0.00") : new BigDecimal(userCoupon.getCouponPrice());
            payPrice = orderPrice.add(orderFreight).subtract(couponPrice);
        }
        insertUserOrderDetail(userId, userName, orderId, null, null, null, ReceiveWayEnum.PS.getCode(), "N", num, itemSku.getItemId(),
                itemSku.getId(), itemSku.getSkuTitle(), itemSku.getSkuSalePrice(), itemSku.getSkuCover(), orderFreight, couponPrice, orderPrice,
                payPrice, userCoupon.getId(), UserOrderStatusEnum.WAIT_PAY.getCode(), itemSku.getSpecValueIdPath(), itemSku.getSpecNameValueJson());

        insertUserOrderAddress(orderId, addressInfo.getMobile(), addressInfo.getRealname(), addressInfo.getProvince(), addressInfo.getProvinceId(),
                addressInfo.getCity(), addressInfo.getCityId(), addressInfo.getDistrict(), addressInfo.getDistrictId(), addressInfo.getAddress());
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

        UserCoupon userCoupon = new UserCoupon();
        if (userCouponId != null) {
            userCoupon = userCouponMapper.selectById(userCouponId);
            this.checkUserCoupon(userCoupon, userCartList.get(0).getItemId());
        }

        // 运费商品等一些金额
        Integer itemCount = 0;
        Integer childOrderCount = userCartList.size();
        BigDecimal couponPrice = new BigDecimal("0.0");
        BigDecimal orderFreight = new BigDecimal("0.0");
        BigDecimal orderPrice = new BigDecimal("0.0");
        BigDecimal payPrice = new BigDecimal("0.0");

        // 扣库存，这里要做手写SQL，搞乐观锁
        Map<Integer, ItemSkuResult> itemSkuMap = new HashMap<>();
        // 保存套餐的商品类型
        Set<Integer> tcCategory = new HashSet<>();
        Set<Integer> dpCategory = new HashSet<>();
        //需要支付运费的商品数量
        int sum = 0;
        for (UserCart userCart : userCartList) {
            ItemSkuResult itemSku = mallService.getItemSkuBySkuId(userCart.getSkuId());
            Asserts.assertFalse(itemSku.getSkuStock() < userCart.getNum(), 500, "商品库存不足");
            if ("TC".equals(itemSku.getSkuType())) {
                tcCategory.add(itemSku.getCategoryId());
            } else {
                dpCategory.add(itemSku.getCategoryId());
            }
            checkPrizeAddress(userCart.getSkuId(), addressInfo.getProvince());
            mallService.updateItemSkuStock(itemSku.getId(), userCart.getNum());
            itemSkuMap.put(itemSku.getId(), itemSku);
            itemCount += userCart.getNum();
            // orderFreight = orderFreight.add(itemSku.getFreight().multiply(new BigDecimal(userCart.getNum())));
            orderPrice = orderPrice.add((itemSku.getSkuSalePrice().multiply(new BigDecimal(userCart.getNum()))));
            //payPrice = orderFreight.add(orderPrice);
            if (itemSku.getCategoryId() != 3) {
                sum += userCart.getNum();
            }
        }
        //检测是否只包含单品
        dpCategory.forEach(item -> {
            Asserts.assertTrue(tcCategory.contains(item), 500, "该商品不能单独购买，请添加同类套餐一起购买");
        });
        logger.info("订单价格={}", orderPrice.longValue());
        payPrice = orderPrice;
        // 修改优惠券状态
        if (userCoupon.getId() != null) {
            frontUserCouponService.useUserCoupon(userCoupon.getId());
            couponPrice = new BigDecimal(userCoupon.getCouponPrice());
            logger.info("优惠券抵扣={}", couponPrice.longValue());
            payPrice = payPrice.subtract(couponPrice);
            logger.info("减扣后价格={}", payPrice.longValue());
            if (userCoupon.getCanUseItemIds().contains("2032")) {
                orderFreight = orderFreight.add(new BigDecimal("1.8"));
                sum = sum - 1;
            } else if (userCoupon.getCanUseItemIds().contains("2030")) {
                orderFreight = orderFreight.add(new BigDecimal("18"));
                sum = sum - 1;
            }
        }
        logger.info("运费价格={}", orderFreight.longValue());
        // 删除购物车id
        List<Integer> skuIdList = userCartList.stream().map(UserCart::getSkuId).collect(Collectors.toList());
        userCartService.deleteUserCarts(userId, skuIdList);
        //平均运费价格
        BigDecimal freight = BigDecimal.ZERO;
        if (payPrice.longValue() < 88) {
            freight = new BigDecimal("10");
            if (sum > 0) {
                orderFreight = orderFreight.add(new BigDecimal("10"));
                freight = freight.divide(new BigDecimal(sum), 2, BigDecimal.ROUND_HALF_UP);
            }
        }
        payPrice = orderFreight.add(payPrice);
        // 创建订单
        logger.info("支付订单价格={}", payPrice.longValue());
        Order order = insertUserOrder(userId, null, ReceiveWayEnum.PS.getCode(), itemCount, childOrderCount, orderPrice, couponPrice, orderFreight, payPrice, "N", null);
        Long orderId = order.getId();
        for (UserCart userCart : userCartList) {
            ItemSkuResult itemSku = itemSkuMap.get(userCart.getSkuId());
            BigDecimal childCouponPrice = new BigDecimal("0.0");
            String userName = null;
            User user = userMapper.selectById(userId);
            if (user != null) {
                userName = user.getNickname();
            }
            //二等奖数量大于1时，使用优惠券必须拆单为一瓶装
            if ((itemSku.getId().equals(2032001) || itemSku.getId().equals(2030001)) && userCoupon.getId() != null && userCart.getNum() > 1) {
                BigDecimal childPayPrice = itemSku.getSkuSalePrice().add(itemSku.getFreight()).subtract(childCouponPrice);
                insertUserOrderDetail(userId, userName, orderId, null, null, null, ReceiveWayEnum.PS.getCode(), "N", 1,
                        itemSku.getItemId(), itemSku.getId(), itemSku.getSkuTitle(), itemSku.getSkuSalePrice(), itemSku.getSkuCover(), itemSku.getFreight(), childCouponPrice,
                        itemSku.getSkuSalePrice(), childPayPrice, userCouponId, UserOrderStatusEnum.WAIT_PAY.getCode(), itemSku.getSpecValueIdPath(), itemSku.getSpecNameValueJson());
                userCoupon.setCouponId(null);
                userCoupon.setId(null);
                userCoupon.setCouponPrice(null);
                userCart.setNum(userCart.getNum() - 1);
            }
            BigDecimal childOrderFreight = BigDecimal.ZERO;
            if (userCoupon.getId() != null) {
                childCouponPrice = new BigDecimal(userCoupon.getCouponPrice());
                if ((itemSku.getId().equals(2032001) || itemSku.getId().equals(2030001))) {
                    childOrderFreight = itemSku.getFreight();
                }
            } else {
                childOrderFreight = freight;
            }
            BigDecimal childOrderPrice = itemSku.getSkuSalePrice().multiply(new BigDecimal(userCart.getNum()));
            BigDecimal childPayPrice = childOrderPrice.add(childOrderFreight).subtract(childCouponPrice);
            insertUserOrderDetail(userId, userName, orderId, null, null, null, ReceiveWayEnum.PS.getCode(), "N", userCart.getNum(),
                    itemSku.getItemId(), itemSku.getId(), itemSku.getSkuTitle(), itemSku.getSkuSalePrice(), itemSku.getSkuCover(), childOrderFreight, childCouponPrice,
                    childOrderPrice, childPayPrice, userCouponId, UserOrderStatusEnum.WAIT_PAY.getCode(), itemSku.getSpecValueIdPath(), itemSku.getSpecNameValueJson());

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
            this.checkUserCoupon(dataList.get(0), itemIdList.get(0));
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
        if ("pro".equalsIgnoreCase(SpringUtil.getActiveProfile())) {
            orderRequest.setTotalFee(BaseWxPayRequest.yuanToFen(order.getPayPrice().toString()));
        } else {
            orderRequest.setTotalFee(1);
        }
        orderRequest.setTimeStart(DateFormatUtils.format(new Date(), "yyyyMMddHHmmss"));
        orderRequest.setOutTradeNo(PayPrefixEnum.USER_ORDER.getPrefix() + order.getId() + "-" + order.getPayEntryCount());
        orderRequest.setTimeExpire(DateFormatUtils.format(new Date(System.currentTimeMillis() + (1000 * 60 * 15)), "yyyyMMddHHmmss"));
        WxPayMpOrderResult payOrderResult = mpPayService.createPayOrder(orderRequest);
        // 修改订单支付重试次数, 防止唤起支付后不立马支付
        orderDao.updateOrderPayEntryCount(orderId);
        Order o = new Order();
        o.setId(orderId);
        o.setOutOrderNo(orderRequest.getOutTradeNo());
        orderMapper.updateById(o);
        return payOrderResult;
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
    private void checkUserCoupon(UserCoupon userCoupon, Integer itemId) throws ApiException {
        Asserts.assertNonNull(userCoupon, 500, "用户优惠券不存在");
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
}

