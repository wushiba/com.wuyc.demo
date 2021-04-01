package com.yfshop.shop.service.order;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yfshop.code.mapper.*;
import com.yfshop.code.model.*;
import com.yfshop.common.enums.UserCouponStatusEnum;
import com.yfshop.common.enums.UserOrderStatusEnum;
import com.yfshop.common.exception.ApiException;
import com.yfshop.common.exception.Asserts;
import com.yfshop.common.util.BeanUtil;
import com.yfshop.shop.service.order.result.YfUserOrderDetailResult;
import com.yfshop.shop.service.order.result.YfUserOrderListResult;
import com.yfshop.shop.service.order.service.FrontUserOrderService;
import org.apache.dubbo.config.annotation.Service;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
    private OrderMapper orderMapper;

    @Resource
    private ItemSkuMapper itemSkuMapper;

    @Resource
    private UserCouponMapper userCouponMapper;

    @Resource
    private OrderDetailMapper orderDetailMapper;

    @Resource
    private OrderAddressMapper orderAddressMapper;

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
     * @param userId		用户id
     * @param orderId		订单id
     * @param orderDetailId
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
                .eq(OrderDetail::getUserId, userId).eq(OrderDetail::getOrderId, orderId).orderByDesc(OrderDetail::getId));

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
    public void cancelOrder(Integer userId, Integer orderId) throws ApiException {
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
    }

    /**
     * 用户确认订单
     * @param userId			用户id
     * @param orderDetailId		订单详情id
     * @throws ApiException
     */
    @Override
    public void confirmOrder(Integer userId, Integer orderDetailId) throws ApiException {
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
    public Void submitOrderBySkuId(Integer userId, Integer skuId, Integer num, Long userCouponId, Long addressId) throws ApiException {
        ItemSku itemSku = itemSkuMapper.selectOne(Wrappers.lambdaQuery(ItemSku.class).eq(ItemSku::getId, skuId));
        Asserts.assertNonNull(itemSku, 500, "商品sku不存在");
        Asserts.assertFalse(itemSku.getSkuStock() < num, 500, "商品库存不足");

        OrderAddress orderAddress = orderAddressMapper.selectOne(Wrappers.lambdaQuery(OrderAddress.class)
                .eq(OrderAddress::getId, addressId));
        Asserts.assertNonNull(orderAddress, 500, "收货地址不存在");

        UserCoupon userCoupon = userCouponMapper.selectOne(Wrappers.lambdaQuery(UserCoupon.class).eq(UserCoupon::getId, userCouponId));
        Asserts.assertNonNull(userCoupon, 500, "优惠券不存在");
        Asserts.assertEquals(userCoupon.getUseStatus(), UserCouponStatusEnum.NO_USE.getCode(), 500, "优惠券状态不正确");
        Asserts.assertFalse(userCoupon.getValidEndTime().isAfter(LocalDateTime.now()), 500, "优惠券已过期");

        // 扣库存，这里要做手写SQL，搞乐观锁
        itemSku.setSkuStock(itemSku.getSkuStock() - num);
        itemSkuMapper.updateById(itemSku);

        // 下单，创建订单，订单详情，收货地址



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
    public Void submitOrderByCart(Integer userId, String cartIds, Long userCouponId, Long addressId) throws ApiException {
        return null;
    }

    /**
     * 优惠券购买商品
     * @param userId		用户id
     * @param userCouponIds	用户优惠券ids
     * @param userMobile	用户手机号
     * @param websiteCode	商户网点码
     * @return
     * @throws ApiException
     */
    @Override
    public Void submitOrderByUserCouponId(Integer userId, String userCouponIds, String userMobile, String websiteCode) throws ApiException {
        return null;
    }


    //----------------------------------------------------- private method ---------------------------------------------------------------------------------

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

}

