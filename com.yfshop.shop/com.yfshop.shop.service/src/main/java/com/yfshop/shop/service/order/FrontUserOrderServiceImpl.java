package com.yfshop.shop.service.order;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yfshop.code.mapper.*;
import com.yfshop.code.model.*;
import com.yfshop.common.enums.ReceiveWayEnum;
import com.yfshop.common.enums.UserCouponStatusEnum;
import com.yfshop.common.enums.UserOrderStatusEnum;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.exception.Asserts;
import com.yfshop.common.util.BeanUtil;
import com.yfshop.shop.service.activity.result.YfDrawActivityResult;
import com.yfshop.shop.service.activity.result.YfDrawPrizeResult;
import com.yfshop.shop.service.activity.service.FrontDrawService;
import com.yfshop.shop.service.address.result.UserAddressResult;
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
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Title:用户订单Service实现
 * @Description:
 * @Author:Wuyc
 * @Since:2021-03-31 16:16:25
 * @Version:1.1.0
 */
@Service(dynamic = true)
public class FrontUserOrderServiceImpl implements FrontUserOrderService {

    @Resource
    private ItemMapper itemMapper;

    @Resource
    private MallService mallService;

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private UserCartMapper userCartMapper;

    @Resource
    private ItemSkuMapper itemSkuMapper;

    @Resource
    private FrontDrawService frontDrawService;

    @Resource
    private UserCouponMapper userCouponMapper;

    @Resource
    private OrderDetailMapper orderDetailMapper;

    @Resource
    private OrderAddressMapper orderAddressMapper;

    @Resource
    private FrontUserService frontUserService;

    @Resource
    private FrontMerchantService frontMerchantService;

    /**
     * 校验提交订单的时候是否支持自提
     * @param userId	用户id
     * @param itemId	商品id
     * @return 支持自提，返回true， 否则返回false
     */
    @Override
    public Boolean checkSubmitOrderIsCanZt(Integer userId, Integer itemId, Integer skuId) {
        UserCoupon userCoupon = userCouponMapper.selectOne(Wrappers.lambdaQuery(UserCoupon.class)
                .eq(UserCoupon::getUserId, userId)
                .eq(UserCoupon::getDrawPrizeLevel, 2)
                .gt(UserCoupon::getValidEndTime, new Date())
                .eq(UserCoupon::getUseStatus, UserCouponStatusEnum.NO_USE.getCode()));
        if (userCoupon == null) {
            return false;
        }

        if ("ALL".equalsIgnoreCase(userCoupon.getUseRangeType()) || userCoupon.getCanUseItemIds().contains(itemId + "")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 查询用户所有订单, 根据订单状态去组装. 因为单个用户不可能会有很多订单
     * 待付款，已取消状态的订单，主订单信息是order里的数据， 子订单是orderDetail
     * 待发货，待收货，已完成的订单，主订单是detail 里的数据， 子订单是orderDetail
     * @param userId		用户id
     * @param useStatus		订单状态
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
     * @param userId		    用户id
     * @param orderId		    订单id
     * @param orderDetailId     订单详情id
     * @return
     * @throws ApiException
     */
    @Override
    public YfUserOrderDetailResult getUserOrderDetail(Integer userId, Integer orderId, Integer orderDetailId) throws ApiException {
        // todo 量大的话可以做1分钟秒缓存
        Asserts.assertFalse(orderId == null && orderDetailId == null , 500, "订单标识不可以为空");
        YfUserOrderDetailResult userOrderDetailResult;

        Order order = orderMapper.selectOne(Wrappers.lambdaQuery(Order.class)
                .eq(Order::getUserId, userId)
                .eq(Order::getId, orderId));
        List<OrderDetail> detailList = orderDetailMapper.selectList(Wrappers.lambdaQuery(OrderDetail.class)
                .eq(OrderDetail::getUserId, userId)
                .eq(OrderDetail::getOrderId, orderId)
                .orderByDesc(OrderDetail::getId));

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
     * @param userId	用户id
     * @param orderId	订单id
     * @throws ApiException
     */
    @Override
    public Void cancelOrder(Integer userId, Integer orderId) throws ApiException {
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
            }
        });
        return null;
    }

    /**
     * 用户确认订单
     * @param userId			用户id
     * @param orderDetailId		订单详情id
     * @throws ApiException
     */
    @Override
    public Void confirmOrder(Integer userId, Integer orderDetailId) throws ApiException {
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
        }
        return null;
    }

    /**
     * 商品单个立即购买
     * @param userId	    用户id
     * @param skuId		    skuId
     * @param num		    购买数量
     * @param userCouponId	用户优惠券id
     * @param addressId	    用户地址id
     * @return
     * @throws ApiException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Void submitOrderBySkuId(Integer userId, Integer skuId, Integer num, Long userCouponId, Integer addressId) throws ApiException {
        // 校验sku以及商品
        ItemSkuResult itemSku = mallService.getItemSkuBySkuId(skuId);
        Asserts.assertFalse(itemSku.getSkuStock() < num, 500, "商品库存不足");
        QueryItemDetailReq req = new QueryItemDetailReq();
        req.setItemId(itemSku.getItemId());
        ItemResult itemResult = mallService.findItemDetail(req);

        UserAddressResult addressInfo = frontUserService.getUserAddressById(addressId);
        Asserts.assertNonNull(addressInfo, 500, "收货地址不存在");

        UserCoupon userCoupon = new UserCoupon();
        if (userCouponId != null) {
            userCoupon = userCouponMapper.selectOne(Wrappers.lambdaQuery(UserCoupon.class).eq(UserCoupon::getId, userCouponId));
            this.checkUserCoupon(userCoupon);
        }

        // 扣库存
        mallService.updateItemSkuStock(itemSku.getId(), num);

        if (userCoupon.getId() != null) {
            userCoupon.setUseStatus(UserCouponStatusEnum.IN_USE.getCode());
            int updateStatus = userCouponMapper.updateById(userCoupon);
            Asserts.assertFalse(updateStatus < 1, 500, "优惠券状态不正确，请稍后重试");
        }

        // 下单，创建订单，订单详情，收货地址
        BigDecimal itemFreight = itemResult.getFreight();
        BigDecimal orderFreight = new BigDecimal(num).multiply(itemFreight);
        BigDecimal orderPrice = new BigDecimal(num).multiply(itemSku.getSkuSalePrice());
        BigDecimal couponPrice = userCoupon.getCouponPrice() == null ? new BigDecimal("0.00") : new BigDecimal(userCoupon.getCouponPrice());
        BigDecimal payPrice = orderPrice.add(orderFreight).subtract(couponPrice);

        Order order = insertUserOrder(userId, ReceiveWayEnum.PS.getCode(), num, 1, orderPrice, couponPrice, orderFreight, payPrice, "N", null);
        Long orderId = order.getId();

        insertUserOrderDetail(userId, orderId, null, null, ReceiveWayEnum.ZT.getCode(), "N", 1, itemSku.getItemId(), itemSku.getId(),
                itemResult.getItemTitle(), itemSku.getSkuSalePrice(), itemSku.getSkuCover(), itemFreight, itemSku.getSkuSalePrice(), itemSku.getSkuSalePrice(),
                itemFreight, userCoupon.getId(), UserOrderStatusEnum.WAIT_PAY.getCode(), null, itemSku.getSpecNameValueJson());

        insertUserOrderAddress(orderId, addressInfo.getMobile(), addressInfo.getRealname(), addressInfo.getProvince(), addressInfo.getProvinceId(),
                addressInfo.getCity(), addressInfo.getCityId(), addressInfo.getDistrict(), addressInfo.getDistrictId(), addressInfo.getAddress());
        return null;
    }

    /**
     * 商品购物车下单购买
     * @param userId		用户id
     * @param cartIds		购物车id
     * @param userCouponId	用户优惠券id
     * @param addressId		用户地址id
     * @return
     * @throws ApiException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Void submitOrderByCart(Integer userId, String cartIds, Long userCouponId, Integer addressId) throws ApiException {
        List<Integer> cartIdList = Arrays.stream(StringUtils.split(cartIds, ",")).map(Integer::valueOf)
                .collect(Collectors.toList());
        Asserts.assertCollectionNotEmpty(cartIdList, 500, "购物车id不可以为空");
        Asserts.assertFalse(cartIdList.size() > 1 && userCouponId != null, 500, "您不能使用优惠券");

        UserAddressResult addressInfo = frontUserService.getUserAddressById(addressId);
        Asserts.assertNonNull(addressInfo, 500, "收货地址不存在");

        UserCoupon userCoupon = new UserCoupon();
        if (userCouponId != null) {
            userCoupon = userCouponMapper.selectOne(Wrappers.lambdaQuery(UserCoupon.class).eq(UserCoupon::getId, userCouponId));
            this.checkUserCoupon(userCoupon);
        }

        List<UserCart> userCartList = userCartMapper.selectList(Wrappers.lambdaQuery(UserCart.class)
                .eq(UserCart::getUserId, userId).in(UserCart::getId, cartIdList));
        Asserts.assertCollectionNotEmpty(userCartList, 500, "购物车id不正确");
        Asserts.assertEquals(userCartList.size(), cartIdList.size(), 500, "购物车数据不正确，请刷新重试");

        // 运费商品等一些金额
        Integer itemCount = 0;
        Integer childOrderCount = userCartList.size();
        BigDecimal couponPrice = new BigDecimal("0.0");
        BigDecimal orderFreight = new BigDecimal("0.0");
        BigDecimal orderPrice = new BigDecimal("0.0");
        BigDecimal payPrice = new BigDecimal("0.0");

        // 扣库存，这里要做手写SQL，搞乐观锁
        Map<Integer, ItemSku> itemSkuMap = new HashMap<>();
        for (UserCart userCart : userCartList) {
            ItemSku itemSku = itemSkuMapper.selectOne(Wrappers.lambdaQuery(ItemSku.class).eq(ItemSku::getId, userCart.getSkuId()));
            Asserts.assertNonNull(itemSku, 500, "商品sku不存在");
            Asserts.assertFalse(itemSku.getSkuStock() < userCart.getNum(), 500, "商品库存不足");
            mallService.updateItemSkuStock(itemSku.getId(), userCart.getNum());
            itemSkuMap.put(itemSku.getId(), itemSku);
            itemCount += userCart.getNum();
            orderFreight = orderFreight.add(new BigDecimal(userCart.getNum() * 2));
            orderPrice = orderFreight.add((itemSku.getSkuSalePrice().multiply(new BigDecimal(2))));
            payPrice = orderFreight.add(orderPrice);
        }

        // 修改优惠券状态
        if (userCoupon.getId() != null) {
            userCoupon.setUseStatus(UserCouponStatusEnum.IN_USE.getCode());
            int updateStatus = userCouponMapper.updateById(userCoupon);
            Asserts.assertFalse(updateStatus < 1, 500, "优惠券状态不正确，请稍后重试");
            couponPrice = new BigDecimal(userCoupon.getCouponPrice());
            payPrice = payPrice.subtract(couponPrice);
        }

        Order order = insertUserOrder(userId, ReceiveWayEnum.ZT.getCode(), itemCount, childOrderCount, orderPrice, couponPrice, orderFreight, payPrice, "N", null);
        Long orderId = order.getId();
        for (UserCart userCart : userCartList) {
            ItemSku itemSku = itemSkuMap.get(userCart.getSkuId());
            BigDecimal childCouponPrice = new BigDecimal("0.0");
            BigDecimal childOrderFreight = new BigDecimal(userCart.getNum() * 2);
            BigDecimal childOrderPrice = itemSku.getSkuSalePrice().multiply(new BigDecimal(userCart.getNum()));
            BigDecimal childPayPrice = childOrderPrice.add(childOrderFreight).subtract(childCouponPrice);

            insertUserOrderDetail(userId, orderId, null, null, ReceiveWayEnum.PS.getCode(), "N", userCart.getNum(),
                    itemSku.getItemId(), itemSku.getId(), itemSku.getSkuTitle(), itemSku.getSkuSalePrice(), itemSku.getSkuCover(), childOrderFreight, childCouponPrice,
                    childOrderPrice, childPayPrice, userCoupon.getId(), UserOrderStatusEnum.WAIT_PAY.getCode(), null, itemSku.getSpecNameIdValueIdJson());
        }

        insertUserOrderAddress(orderId, addressInfo.getMobile(), addressInfo.getRealname(), addressInfo.getProvince(), addressInfo.getProvinceId(), addressInfo.getCity(),
                addressInfo.getCityId(), addressInfo.getDistrict(), addressInfo.getDistrictId(), addressInfo.getAddress());

        // 删除购物车id
        userCartMapper.deleteBatchIds(cartIdList);
        return null;
    }

    /**
     * 优惠券购买商品
     * @param userId		用户id
     * @param userCouponIds	用户优惠券ids(只有二等奖可以自提)
     * @param userMobile	用户手机号
     * @param websiteCode	商户网点码
     * @return
     * @throws ApiException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Void submitOrderByUserCouponIds(Integer userId, String userCouponIds, String userMobile, String websiteCode) throws ApiException {
        // 校验网点码商户
        MerchantResult merchantResult = frontMerchantService.getMerchantByWebsiteCode(websiteCode);

        // 校验用户优惠券
        List<Long> userCouponIdList = Arrays.stream(StringUtils.split(userCouponIds, ","))
                .map(Long::valueOf).collect(Collectors.toList());
        Asserts.assertCollectionNotEmpty(userCouponIdList, 500, "用户优惠券id不可以为空");
        List<UserCoupon> userCouponList = userCouponMapper.selectList(Wrappers.lambdaQuery(UserCoupon.class)
                .in(UserCoupon::getId, userCouponIdList));
        Asserts.assertCollectionNotEmpty(userCouponList, 500, "用户优惠券查询不到");
        Map<Long, List<UserCoupon>> userCouponMap = userCouponList.stream().collect(Collectors.groupingBy(UserCoupon::getId));
        for (Long userCouponId : userCouponIdList) {
            List<UserCoupon> dataList = userCouponMap.get(userCouponId);
            Asserts.assertCollectionNotEmpty(dataList, 500, "用户优惠券不存在");
            this.checkUserCoupon(dataList.get(0));
        }

        // 校验抽奖活动,当前有且仅有一个活动进行中
        Set<Integer> actIdSetList = userCouponList.stream().map(UserCoupon::getDrawActivityId).collect(Collectors.toSet());
        Asserts.assertFalse(actIdSetList.size() > 1, 500, "请选择正确的活动");
        YfDrawActivityResult drawActivityResult = frontDrawService.getDrawActivityDetailById(actIdSetList.iterator().next());
        Asserts.assertNonNull(drawActivityResult, 500, "此活动不存在,请联系管理员处理");
        Set<Integer> couponIdSetList = userCouponList.stream().map(UserCoupon::getCouponId).collect(Collectors.toSet());
        Asserts.assertFalse(couponIdSetList.size() > 1, 500, "请传入正确的用户优惠券");
        YfDrawPrizeResult yfDrawPrizeResult = drawActivityResult.getPrizeList().stream().filter(prize ->
                prize.getPrizeLevel() == 2).collect(Collectors.toList()).get(0);
        Asserts.assertTrue(userCouponList.get(0).getCouponId().intValue() == yfDrawPrizeResult.getCouponId().intValue(),
                500, "自提奖品只支持二等奖");

        // 二等奖是一个商品，这个sku必须是单规格
        List<Integer> itemIdList = userCouponList.stream().map(UserCoupon::getCanUseItemIds).map(Integer::valueOf).collect(Collectors.toList());
        QueryItemDetailReq req = new QueryItemDetailReq();
        req.setItemId(itemIdList.get(0));
        ItemResult itemDetail = mallService.findItemDetail(req);
        ItemSkuResult itemSku = itemDetail.getItemSkuList().get(0);

        // 扣优惠券对应的商品库存
        mallService.updateItemSkuStock(itemSku.getId(), userCouponIdList.size());

        // 修改优惠券状态

        // 根据优惠券计算订单金额，创建订单,子订单, 收货地址 一个优惠券对应一个子订单，一个子订单运费2块钱
        Integer itemCount = userCouponIdList.size();
        BigDecimal itemFreight = new BigDecimal("2");
        BigDecimal orderFreight = new BigDecimal(itemCount).multiply(itemFreight);
        BigDecimal orderPrice = new BigDecimal(itemCount).multiply(itemSku.getSkuSalePrice()).setScale(2, BigDecimal.ROUND_UP);

        Order order = insertUserOrder(userId, ReceiveWayEnum.ZT.getCode(), itemCount, itemCount, orderPrice, orderPrice, orderFreight, orderFreight, "N", null);
        Long orderId = order.getId();
        userCouponList.forEach(userCoupon -> {
            insertUserOrderDetail(userId, orderId, merchantResult.getId(), merchantResult.getPidPath(), ReceiveWayEnum.ZT.getCode(), "N", 1,
                    itemSku.getItemId(), itemSku.getId(), itemDetail.getItemTitle(), itemSku.getSkuSalePrice(), itemSku.getSkuCover(), itemFreight, itemSku.getSkuSalePrice(),
                    itemSku.getSkuSalePrice(), itemFreight, userCoupon.getId(), UserOrderStatusEnum.WAIT_PAY.getCode(), null, itemSku.getSpecNameIdValueIdJson());
        });
        insertUserOrderAddress(orderId, userMobile, userMobile, merchantResult.getProvince(), merchantResult.getProvinceId(), merchantResult.getCity(),
                merchantResult.getCityId(), merchantResult.getDistrict(), merchantResult.getDistrictId(), merchantResult.getAddress());
        return null;
    }


    //----------------------------------------------------- private method ---------------------------------------------------------------------------------

    /**
     * 组装前台c端用户展示的通用订单数据
     * @param orderList     订单列表
     * @param childList     子订单列表
     * @return
     */
    private List<YfUserOrderListResult> setUserOrderListResult(List<Order> orderList, List<OrderDetail> childList) {
        List<YfUserOrderListResult> resultList = new ArrayList<>();
        for (Order order : orderList) {
            YfUserOrderListResult orderResult;
            List<OrderDetail> detailList = childList.stream().filter(data -> data.getOrderId().equals(order.getId())).collect(Collectors.toList());
            if ("N".equalsIgnoreCase(order.getIsPay()) && "N".equalsIgnoreCase(order.getIsCancel())) {
                // 未付款订单状态组装
                orderResult = BeanUtil.convert(order, YfUserOrderListResult.class);
                orderResult.setOrderId(order.getId());
                orderResult.setOrderStatus(UserOrderStatusEnum.WAIT_PAY.getCode());
                orderResult.setItemList(BeanUtil.convertList(detailList, YfUserOrderListResult.YfUserOrderItem.class));
                resultList.add(orderResult);
            } else if ("N".equalsIgnoreCase(order.getIsPay()) && "Y".equalsIgnoreCase(order.getIsCancel())) {
                // 已取消订单状态组装
                orderResult = BeanUtil.convert(order, YfUserOrderListResult.class);
                orderResult.setOrderId(order.getId());
                orderResult.setOrderStatus(UserOrderStatusEnum.CANCEL.getCode());
                orderResult.setItemList(BeanUtil.convertList(detailList, YfUserOrderListResult.YfUserOrderItem.class));
                resultList.add(orderResult);
            } else {
                // 待发货，待收货，已完成订单状态组装数据
                for (OrderDetail orderDetail : detailList) {
                    orderResult = BeanUtil.convert(orderDetail, YfUserOrderListResult.class);
                    orderResult.setOrderDetailId(orderDetail.getId());
                    orderResult.setItemList(BeanUtil.convertList(Arrays.asList(orderDetail), YfUserOrderListResult.YfUserOrderItem.class));
                    resultList.add(orderResult);
                }
            }
        }
        return resultList;
    }


    /**
     * 创建用户订单
     * @param userId            用户id
     * @param receiveWay        收货方式 ZT | PS
     * @param itemCount         商品数量
     * @param childOrderCount   子订单数量
     * @param orderPrice        订单金额
     * @param couponPrice       优惠券金额
     * @param freight           运费
     * @param payPrice          实际支付金额
     * @param isPay             是否支付
     * @param remark
     * @return  Order
     */
    private Order insertUserOrder(Integer userId, String receiveWay, Integer itemCount, Integer childOrderCount, BigDecimal orderPrice,
                                  BigDecimal couponPrice, BigDecimal freight, BigDecimal payPrice, String isPay, String remark) {
        Order order = new Order();
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        order.setUserId(userId);
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
    private OrderDetail insertUserOrderDetail(Integer userId, Long orderId, Integer merchantId, String pidPath, String receiveWay,
                                              String isPay, Integer itemCount, Integer itemId, Integer skuId, String itemTitle, BigDecimal skuPrice,
                                              String itemCover, BigDecimal freight, BigDecimal couponPrice, BigDecimal orderPrice,  BigDecimal payPrice,
                                              Long userCouponId, String orderStatus, String specValueIdPath, String specNameValueJson) {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setCreateTime(LocalDateTime.now());
        orderDetail.setUserId(userId);
        orderDetail.setOrderId(orderId);
        orderDetail.setMerchantId(merchantId);
        orderDetail.setPidPath(pidPath);
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

        orderDetailMapper.insert(orderDetail);
        return orderDetail;
    }

    /**
     * 创建订单收货地址
     * @param orderId       订单id
     * @param mobile        手机号
     * @param realname      收货人姓名
     * @param province      省份
     * @param provinceId    省id
     * @param city          市
     * @param cityId        市id
     * @param district      区
     * @param districtId    区id
     * @param address       详细地址
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
     * @param userCoupon
     * @throws ApiException
     */
    private void checkUserCoupon(UserCoupon userCoupon) throws ApiException {
        Asserts.assertNonNull(userCoupon, 500, "用户优惠券不存在");
        Asserts.assertFalse(userCoupon.getValidEndTime().isBefore(LocalDateTime.now()), 500, "优惠券已过期");
        Asserts.assertEquals(userCoupon.getUseStatus(), UserCouponStatusEnum.NO_USE.getCode(), 500, "优惠券状态不正确");
    }

}

